package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.inversoft.error.Errors
import com.wks.servicemarketplace.authservice.config.FusionAuthConfiguration
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.dtos.SignInRequest
import com.wks.servicemarketplace.authservice.core.errors.ErrorType
import com.wks.servicemarketplace.authservice.core.errors.RegistrationFailedException
import com.wks.servicemarketplace.authservice.core.errors.LoginFailedException
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.Group
import io.fusionauth.domain.GroupMember
import io.fusionauth.domain.UserRegistration
import io.fusionauth.domain.api.LoginRequest
import io.fusionauth.domain.api.LoginResponse
import io.fusionauth.domain.api.MemberRequest
import io.fusionauth.domain.api.user.RegistrationRequest
import io.fusionauth.domain.api.user.RegistrationResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

class FusionAuthAdapter @Inject constructor(
        private val config: FusionAuthConfiguration
) : IAMAdapter {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(FusionAuthAdapter::class.java)
    }

    private val fusionAuthUserClient = FusionAuthClient(config.apiKey, config.serverUrl, config.tenantId)
    private val groups by lazy { loadGroups() }

    override fun login(credentials: Credentials): User {
        val login = getUser(credentials)
        val group = groups.first { it.id == login.user.memberships.first().groupId }

        return FusionAuthUser(
                login.user.id.toString(),
                login.user.firstName,
                login.user.lastName,
                login.user.username,
                login.user.email,
                group.name,
                UserType.of(login.user.data["userType"].toString()),
                login.user.registrations.first { it.applicationId == UUID.fromString(config.applicationId) }.roles.toList()
        )
    }

    private fun getUser(credentials: Credentials): LoginResponse {
        val response = fusionAuthUserClient.login(
                LoginRequest(
                        UUID.fromString(config.applicationId),
                        credentials.username,
                        credentials.password
                )
        ).also {
            LOGGER.info("Login: Username: {}. Status: {}. Error: {}. Exception: {}", it.status, credentials.username, it.errorResponse, it.exception)
        }
        when {
            response.wasSuccessful() -> return response.successResponse
            response.status == 404 -> throw LoginFailedException(message = "User does not exist or password incorrect", errorType = ErrorType.NOT_FOUND)
            response.errorResponse != null -> throw LoginFailedException(fields = response.errorResponse.allErrors(), errorType = response.errorResponse.errorType())
            response.exception != null -> throw LoginFailedException(message = response.exception.message, errorType = ErrorType.UNKNOWN)
            else -> throw LoginFailedException(message = "Login Failed", errorType = ErrorType.UNKNOWN)
        }
    }

    override fun register(registration: Registration): User {
        val actualRole = when (registration.userType) {
            UserType.CUSTOMER -> registration.userType.code
            UserType.SERVICE_PROVIDER -> "ServiceProvider.ProfilePending"
        }

        val user = createUser(registration).user
        val permisions = assignGroup(actualRole, user.id.toString())
        return FusionAuthUser(
                user.id.toString(),
                user.username,
                user.firstName,
                user.lastName,
                user.email,
                actualRole,
                registration.userType,
                permisions
        )
    }

    private fun createUser(registration: Registration): RegistrationResponse {
        val id = UUID.randomUUID()

        val response = fusionAuthUserClient.register(
                id,
                RegistrationRequest(
                        io.fusionauth.domain.User().with {
                            it.email = registration.email
                            it.email = registration.email
                            it.firstName = registration.firstName
                            it.lastName = registration.lastName
                            it.username = registration.username
                            it.password = registration.password
                            it.data = mapOf("userType" to registration.userType.code)
                        },
                        UserRegistration().with {
                            it.applicationId = UUID.fromString(config.applicationId)
                            it.username = registration.username
                        }
                )
        ).also {
            LOGGER.info("Register: Username: {}. Error: {}. Exception: {}", registration.username, it.errorResponse, it.exception)
        }

        when {
            response.wasSuccessful() -> return response.successResponse
            response.errorResponse != null -> throw RegistrationFailedException(response.errorResponse.allErrors(), errorType = response.errorResponse.errorType())
            response.exception != null -> throw RegistrationFailedException(message = response.exception.message, errorType = ErrorType.UNKNOWN)
            else -> throw RegistrationFailedException(message = "Registration Failed", errorType = ErrorType.UNKNOWN)
        }
    }

    private fun loadGroups(): List<Group> {
        val response = fusionAuthUserClient.retrieveGroups()
                .also { LOGGER.info("Retrieve groups. Status: {}. Error: {}. Exception: {}", it.status, it.errorResponse, it.exception) }

        if (!response.wasSuccessful()) {
            throw RuntimeException("Failed to retrieve groups")
        }
        return response.successResponse.groups
    }

    private fun assignGroup(role: String, userId: String): List<String> {

        val group = groups.firstOrNull { it.name == role } ?: throw RuntimeException("Role not found")
        val response = fusionAuthUserClient.createGroupMembers(MemberRequest(group.id, listOf(GroupMember().with {
            it.userId = UUID.fromString(userId)
            it.groupId = group.id
        }))).also {
            LOGGER.info("Add user $userId to group. Status: {}. Error: {}. Exception: {}", it.status, it.errorResponse, it.exception)
        }

        if (!response.wasSuccessful()) {
            throw RuntimeException("Failed to add user $userId to group")
        }

        return group.roles.values.flatten().map { it.name }.toList()
    }

    /**
     * FusionAuth does not support client credentials.
     * Official Workaround: https://github.com/FusionAuth/fusionauth-issues/issues/155
     *
     */
    override fun apiToken(clientCredentials: ClientCredentials): Client {
        return getUser(SignInRequest(clientCredentials.clientId, clientCredentials.clientSecret))
                .let { it.user }
                .let { FusionAuthM2MClient(it.username, it.getRoleNamesForApplication(UUID.fromString(config.applicationId)).toList()) }
    }
}

fun Errors.isDuplicateUsername() = allCodes().let { it.contains("[duplicate]user.email") || it.contains("[duplicate]user.username") }

fun Errors.errorType(): ErrorType {
    if (isDuplicateUsername()) return ErrorType.DUPLICATE_USERNAME
    if (fieldErrors.isNotEmpty()) {
        return ErrorType.VALIDATION
    }
    return ErrorType.UNKNOWN
}

fun Errors.allErrors(): Map<String, List<String>> {

    val validations = fieldErrors.map { it.key to it.value.map { it.message } }.toMap()
    val others = mapOf("general" to this.generalErrors.map { it.message }).toMap()

    return validations.plus(others)
}

fun Errors.allCodes(): List<String> {
    return fieldErrors.values.flatten().map { it.code }
}