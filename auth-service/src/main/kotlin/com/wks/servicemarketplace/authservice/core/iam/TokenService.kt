package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.config.ApplicationParameters
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.dtos.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.errors.ErrorType
import com.wks.servicemarketplace.authservice.core.errors.LoginFailedException
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import java.security.PrivateKey
import java.time.Duration
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey,
                                       private val eventPublisher: EventPublisher,
                                       private val applicationParameters: ApplicationParameters) {

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)
        return StandardToken(
                user.username,
                StandardToken.User(user.id, user.firstName, user.lastName, user.username, user.email, user.role),
                user.permissions,
                Duration.ofHours(1),
                privateKey = privateKey
        )
    }

    fun register(registration: Registration): Identity {
        val identity = iam.register(registration)
        val token = apiToken(ClientCredentialsRequest(
                applicationParameters.clientId,
                applicationParameters.clientSecret,
                listOf("account.create") // TODO use constant
        )).accessToken

        when (identity.type) {
            UserType.CUSTOMER -> eventPublisher.customerAccountCreated(token, AccountCreatedEvent(identity))
            UserType.SERVICE_PROVIDER -> eventPublisher.serviceProviderAccountCreated(token, AccountCreatedEvent(identity))
        }

        return identity
    }

    fun apiToken(clientCredentials: ClientCredentials): Token {
        return iam.apiToken(clientCredentials)
                .takeIf { it.permissions.containsAll(clientCredentials.requestedPermissions) }
                ?.let {
                    StandardToken(
                            subject = it.clientName,
                            expiration = Duration.ofHours(2),
                            permissions = clientCredentials.requestedPermissions,
                            privateKey = privateKey
                    )
                }
                ?: throw LoginFailedException(message = "Request included permissions that client does not have", errorType = ErrorType.AUTHORIZATION)
    }
}
