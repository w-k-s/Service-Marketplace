package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.inversoft.error.Errors
import com.wks.servicemarketplace.authservice.api.ClientCredentials
import com.wks.servicemarketplace.authservice.api.Credentials
import com.wks.servicemarketplace.authservice.api.Registration
import com.wks.servicemarketplace.authservice.config.FusionAuthConfiguration
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserRole
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.errors.toException
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
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject

class FusionAuthAdapter @Inject constructor(
        private val config: FusionAuthConfiguration,
        private val assignGroupRetrier: AssignGroupRetrier
) : IAMAdapter {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(FusionAuthAdapter::class.java)
    }

    private val fusionAuthUserClient = FusionAuthClient(config.apiKey, config.serverUrl, config.tenantId)
    private val groups = loadGroups()

    override fun login(credentials: Credentials): User {
        val login = getUser(credentials.username.value, credentials.password.value)
        val role = groups.firstOrNull { it.id == login.user.memberships.firstOrNull()?.groupId }
                ?.let { UserRole.of(it.name) }

        if (role == null) {
            val userType = UserType.of(login.user.data["userType"].toString())
            val group = groupForRole(registrationRoleFor(userType))

            assignGroupRetrier.retry(group.id, login.user.id.toString())
            throw ErrorType.PROCESSING.toException("Registration is in progress. Try to login later")
        }

        val permissions = login.user.registrations
                .first { it.applicationId == UUID.fromString(config.applicationId) }
                .roles
                .toList()

        return login.user.let {
            FusionAuthUser(
                    UserId.of(it.id),
                    Name.of(it.firstName, it.lastName),
                    Email.of(it.username),
                    Email.of(it.email),
                    PhoneNumber.of(it.mobilePhone),
                    role,
                    UserType.of(it.data["userType"].toString()),
                    permissions
            )
        }
    }

    private fun getUser(username: String, password: String): LoginResponse {
        val response = fusionAuthUserClient.login(
                LoginRequest(
                        UUID.fromString(config.applicationId),
                        username,
                        password
                )
        )

        LOGGER.info(
                "Login: Username: {}. Status: {}. Error: {}. Exception: {}",
                username,
                response.status,
                response.errorResponse,
                response.exception
        )

        when {
            response.wasSuccessful() -> return response.successResponse
            response.status == 404 -> throw ErrorType.RESOURCE_NOT_FOUND.toException("User not found")
            response.errorResponse != null -> throw response.errorResponse.toCoreException()
            response.exception != null -> throw ErrorType.EXTERNAL_SYSTEM.toException(cause = response.exception)
            else -> throw ErrorType.AUTHENTICATION.toException("Login Failed")
        }
    }

    private fun registrationRoleFor(userType: UserType): UserRole {
        return when (userType) {
            UserType.CUSTOMER -> UserRole.CUSTOMER
            UserType.SERVICE_PROVIDER -> UserRole.COMPANY_REPRESENTATIVE
        }
    }

    override fun register(registration: Registration): User {
        val role = registrationRoleFor(registration.userType)

        val user = createUser(registration).user
        val permissions = try {
            assignGroup(role, user.id.toString())
        } catch (e: Exception) {
            emptyList<String>()
        }

        return FusionAuthUser(
                UserId.of(user.id),
                Name.of(user.firstName, user.lastName),
                Email.of(user.username),
                Email.of(user.email),
                PhoneNumber.of(user.mobilePhone),
                role,
                registration.userType,
                permissions
        )
    }

    private fun createUser(registration: Registration): RegistrationResponse {
        val id = UUID.randomUUID()

        val response = fusionAuthUserClient.register(
                id,
                RegistrationRequest(
                        io.fusionauth.domain.User().with {
                            it.email = registration.email.value
                            it.email = registration.email.value
                            it.firstName = registration.name.firstName
                            it.lastName = registration.name.lastName
                            it.username = registration.username
                            it.password = registration.password.value
                            it.mobilePhone = registration.mobileNumber.value
                            it.data = mapOf("userType" to registration.userType.code)
                        },
                        UserRegistration().with {
                            it.applicationId = UUID.fromString(config.applicationId)
                            it.username = registration.username
                        }
                )
        )

        LOGGER.info("Register: Username: {}. Error: {}. Exception: {}",
                registration.username,
                response.errorResponse,
                response.exception
        )

        when {
            response.wasSuccessful() -> return response.successResponse
            response.errorResponse != null -> throw response.errorResponse.toCoreException()
            response.exception != null -> throw ErrorType.EXTERNAL_SYSTEM.toException(cause = response.exception)
            else -> throw ErrorType.UNKNOWN.toException("Registration Failed")
        }
    }

    private fun loadGroups(): List<Group> {
        val response = fusionAuthUserClient.retrieveGroups()

        LOGGER.info(
                "Retrieve groups. Status: {}. Error: {}. Exception: {}",
                response.status,
                response.errorResponse,
                response.exception
        )

        if (!response.wasSuccessful()) {
            throw IllegalStateException("Failed to retrieve groups. Status: ${response.status}. Error: ${response.errorResponse}. Exception: ${response.exception}")
        }
        return response.successResponse.groups
    }

    override fun assignRole(role: UserRole, userId: String) {
        assignGroup(role, userId)
    }

    private fun assignGroup(role: UserRole, userId: String): List<String> {
        val group = groupForRole(role)

        try {
            val response = fusionAuthUserClient.createGroupMembers(MemberRequest(group.id, listOf(GroupMember().with {
                it.userId = UUID.fromString(userId)
                it.groupId = group.id
            })))

            LOGGER.info(
                    "Add user $userId to group. Status: {}. Error: {}. Exception: {}",
                    response.status,
                    response.errorResponse,
                    response.exception
            )

            when {
                response.wasSuccessful() -> return group.roles.values.flatten().map { it.name }.toList()
                response.errorResponse != null -> throw response.errorResponse.toCoreException()
                response.exception != null -> throw ErrorType.EXTERNAL_SYSTEM.toException(cause = response.exception)
                else -> throw ErrorType.UNKNOWN.toException("Failed to assign group")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to assign group: {}", e.message, e)
            assignGroupRetrier.retry(group.id, userId)
            throw e
        }
    }

    private fun groupForRole(role: UserRole) = groups.firstOrNull { it.name == role.code }
            ?: throw RuntimeException("No group corresponds to role '$role'")

    /**
     * FusionAuth does not support client credentials.
     * Official Workaround: https://github.com/FusionAuth/fusionauth-issues/issues/155
     *
     */
    override fun apiToken(clientCredentials: ClientCredentials): Client {
        return getUser(clientCredentials.clientId, clientCredentials.clientSecret)
                .let { it.user }
                .let { FusionAuthM2MClient(it.username, it.getRoleNamesForApplication(UUID.fromString(config.applicationId)).toList()) }
    }

    override fun userRoles(userId: String): List<String> {
        val response = fusionAuthUserClient.retrieveUser(UUID.fromString(userId))
        when {
            response.wasSuccessful() -> return response.successResponse.user.registrations.first { it.applicationId.toString() == config.applicationId }.roles.toList()
            response.errorResponse != null -> throw response.errorResponse.toCoreException()
            response.exception != null -> throw CoreException(ErrorType.UNKNOWN, cause = response.exception)
            else -> throw ErrorType.UNKNOWN.toException("Failed to retrieve user roles")
        }
    }
}

fun Errors.toCoreException(): CoreException {
    val codes = fieldErrors.values.flatten().map { it.code }

    val validations = fieldErrors
            .takeIf { it.isNotEmpty() }
            ?.map { it.key to it.value.joinToString { value -> value.message } }
            ?.toMap()
            ?: emptyMap()

    val general = generalErrors
            .takeIf { it.isNotEmpty() }
            ?.joinToString { it.message }
            ?.let { "general" to it }
            ?.let { mapOf(it) }
            ?: emptyMap()

    val fields = validations.plus(general)
    val messages = fields.map { "${it.key}: ${it.value}" }.joinToString(",")

    val errorType = when {
        codes.contains("[duplicate]") -> ErrorType.NOT_UNIQUE
        fieldErrors.isNotEmpty() -> ErrorType.VALIDATION
        else -> ErrorType.UNKNOWN
    }

    return CoreException(errorType, messages, details = fields)
}