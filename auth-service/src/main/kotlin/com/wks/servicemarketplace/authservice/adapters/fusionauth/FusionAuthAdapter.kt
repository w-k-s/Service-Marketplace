package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.inversoft.error.Errors
import com.wks.servicemarketplace.authservice.config.FusionAuthConfiguration
import com.wks.servicemarketplace.authservice.core.*
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

    private val fusionAuthClient = FusionAuthClient(config.apiKey, config.serverUrl, config.tenantId)
    private val groups by lazy { loadGroups() }

    override fun login(credentials: Credentials): User {
        val login = getUser(credentials)
        val group = groups.first { it.id == login.user.memberships.first().groupId }

        return FusionAuthUser(
                login.user.username,
                group.name,
                login.user.registrations.first { it.applicationId == UUID.fromString(config.applicationId) }.roles.toList(),
                login.token
        )
    }

    private fun getUser(credentials: Credentials): LoginResponse {
        val response = fusionAuthClient.login(
                LoginRequest(
                        UUID.fromString(config.applicationId),
                        credentials.username,
                        credentials.password
                )
        )
        when {
            response.wasSuccessful() -> {
                return response.successResponse
            }
            response.status == 404 -> {
                throw LoginFailedException(message = "User does not exist or password incorrect", errorType = ErrorType.NOT_FOUND)
            }
            response.errorResponse != null -> {
                LOGGER.info("Login: Username: {}. Error: {}", credentials.username, response.errorResponse)
                throw LoginFailedException(fields = response.errorResponse.allErrors(), errorType = response.errorResponse.errorType())
            }
            response.exception != null -> {
                LOGGER.info("Login: Username: {}. Exception: {}", credentials.username, response.exception.message)
                throw LoginFailedException(message = response.exception.message, errorType = ErrorType.UNKNOWN)
            }
            else -> throw LoginFailedException(message = "Login Failed", errorType = ErrorType.UNKNOWN)
        }
    }

    override fun register(registration: Registration): Identity {
        return createUser(registration)
                .also { addUserToGroup(registration.userType, it.id) }
    }

    private fun createUser(registration: Registration): Identity {
        val id = UUID.randomUUID()

        val response = fusionAuthClient.register(
                id,
                RegistrationRequest(
                        io.fusionauth.domain.User().with {
                            it.email = registration.email
                            it.email = registration.email
                            it.firstName = registration.firstName
                            it.lastName = registration.lastName
                            it.username = registration.username
                            it.password = registration.password
                        },
                        UserRegistration().with {
                            it.applicationId = UUID.fromString(config.applicationId)
                            it.username = registration.username
                        }
                )
        )
        when {
            response.wasSuccessful() -> {
                return FusionAuthRegistration(response.successResponse.user.id.toString())
            }
            response.errorResponse != null -> {
                LOGGER.info("Register: Username: {}. Error: {}", registration.username, response.errorResponse)
                throw RegistrationFailedException(response.errorResponse.allErrors(), errorType = response.errorResponse.errorType())
            }
            response.exception != null -> {
                LOGGER.info("Register: Username: {}. Exception: {}", registration.username, response.exception.message)
                throw RegistrationFailedException(message = response.exception.message, errorType = ErrorType.UNKNOWN)
            }
            else -> throw RegistrationFailedException(message = "Registration Failed", errorType = ErrorType.UNKNOWN)
        }
    }

    private fun loadGroups(): List<Group> {
        val response = fusionAuthClient.retrieveGroups()
        if (!response.wasSuccessful()) {
            LOGGER.error(
                    "Failed to retrieve groups. Error: {}. Exception: {}",
                    response.errorResponse,
                    response.exception
            )
            throw RuntimeException("Failed to retrieve groups")
        }
        return response.successResponse.groups
    }

    private fun addUserToGroup(role: UserType, userId: String) {

        val actualRole = when (role) {
            UserType.CUSTOMER -> role.code
            UserType.SERVICE_PROVIDER -> "ProfilePendingServiceProvider"
        }

        val group = groups.firstOrNull { it.name == actualRole } ?: throw RuntimeException("Role not found")
        val response = fusionAuthClient.createGroupMembers(MemberRequest(group.id, listOf(GroupMember().with {
            it.userId = UUID.fromString(userId)
            it.groupId = group.id
        })))
        if (!response.wasSuccessful()) {
            LOGGER.error(
                    "Failed to add user to group. Error: {}. Exception: {}",
                    response.errorResponse,
                    response.exception
            )
            throw RuntimeException("Failed to add user to group")
        }
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