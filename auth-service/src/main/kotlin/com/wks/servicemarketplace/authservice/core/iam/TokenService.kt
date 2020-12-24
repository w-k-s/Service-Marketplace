package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.errors.UnauthorizedException
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import java.util.concurrent.Executors
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey,
                                       private val publicKey: PublicKey,
                                       private val eventPublisher: EventPublisher) {

    companion object {
        val LOGGER : Logger = LoggerFactory.getLogger(TokenService::class.java)
    }

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)
        return StandardToken(
                user.username,
                StandardToken.User(user.id, user.firstName, user.lastName, user.username, user.email, user.role.code),
                user.permissions,
                Duration.ofHours(1),
                privateKey = privateKey
        )
    }

    fun register(registration: Registration): User {
        val user = iam.register(registration)

        Executors.newCachedThreadPool().submit{
            val userToken = StandardToken(
                    user.username,
                    StandardToken.User(user.id, user.firstName, user.lastName, user.username, user.email, user.role.code),
                    user.permissions,
                    Duration.ofHours(1),
                    privateKey = privateKey
            ).accessToken

            when (user.role) {
                UserRole.CUSTOMER -> eventPublisher.customerAccountCreated(userToken, AccountCreatedEvent(user))
                UserRole.COMPANY_REPRESENTATIVE -> eventPublisher.serviceProviderAccountCreated(userToken, AccountCreatedEvent(user))
                else -> LOGGER.error("Non-admin employees aren't supported (and won't be cuz i'm lazy)")
            }
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
