package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.api.ClientCredentials
import com.wks.servicemarketplace.authservice.api.Credentials
import com.wks.servicemarketplace.authservice.api.Registration
import com.wks.servicemarketplace.authservice.core.errors.UnauthorizedException
import com.wks.servicemarketplace.authservice.core.sagas.CreateProfileSaga
import com.wks.servicemarketplace.authservice.messaging.AccountCreatedEvent
import com.wks.servicemarketplace.common.auth.StandardToken
import com.wks.servicemarketplace.common.auth.Token
import com.wks.servicemarketplace.common.auth.User
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
                                       private val createProfileSaga: CreateProfileSaga) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(TokenService::class.java)
    }

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)
        return StandardToken(
                user.username.value,
                StandardToken.User(user.id, user.name.firstName, user.name.lastName, user.username, user.email, user.role.code),
                user.permissions,
                Duration.ofHours(1),
                privateKey = privateKey
        )
    }

    fun register(registration: Registration): User {
        val user = iam.register(registration)

        Executors.newCachedThreadPool().submit {
            createProfileSaga.start(AccountCreatedEvent(user))
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
                ?.let { allowedPermissions.intersect(clientCredentials.requestedPermissions).union(client.permissions) }
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
