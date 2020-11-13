package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.config.ApplicationParameters
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.dtos.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.core.errors.ErrorType
import com.wks.servicemarketplace.authservice.core.errors.LoginFailedException
import com.wks.servicemarketplace.authservice.core.errors.UnauthorizedException
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey,
                                       private val publicKey: PublicKey,
                                       private val eventPublisher: EventPublisher) {

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

    fun register(registration: Registration): User {
        val user = iam.register(registration)

        val userToken = StandardToken(
                user.username,
                StandardToken.User(user.id, user.firstName, user.lastName, user.username, user.email, user.role),
                user.permissions,
                Duration.ofHours(1),
                privateKey = privateKey
        ).accessToken

        when (user.type) {
            UserType.CUSTOMER -> eventPublisher.customerAccountCreated(userToken, AccountCreatedEvent(user))
            UserType.SERVICE_PROVIDER -> eventPublisher.serviceProviderAccountCreated(userToken, AccountCreatedEvent(user))
        }

        return user
    }

    fun apiToken(clientCredentials: ClientCredentials): Token {
        val client = iam.apiToken(clientCredentials)

        /**
         * A machine can act on behalf of a user.
         * However, the machine must prove that the user has allowed the machine to act on its behalf.
         * This can be done by providing a valid user authentication token.
         */
        val claims = clientCredentials.impersonationToken?.let { StandardToken.parseClaims(it, publicKey) }

        /**
         * Grant minimal set of permissions
         */
        val allowedPermissions = claims?.permissions?.let { client.permissions.intersect(it) } ?: client.permissions
        val permissions = clientCredentials.requestedPermissions
                .takeIf { allowedPermissions.containsAll(it) }
                ?.let { allowedPermissions.intersect(clientCredentials.requestedPermissions) }
                ?.toList()
                ?: throw UnauthorizedException("Requested permissions included permissions that client/impersonated user does not have")

        return StandardToken(
                client.clientName,
                claims?.user,
                permissions,
                Duration.ofHours(2),
                privateKey = privateKey
        )
    }
}
