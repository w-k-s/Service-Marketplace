package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.api.ClientCredentials
import com.wks.servicemarketplace.authservice.api.Credentials
import com.wks.servicemarketplace.authservice.api.Registration
import com.wks.servicemarketplace.authservice.core.sagas.CreateProfileSaga
import com.wks.servicemarketplace.authservice.messaging.AccountCreatedEvent
import com.wks.servicemarketplace.common.auth.StandardToken
import com.wks.servicemarketplace.common.auth.Token
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.errors.UnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.time.Duration
import java.util.concurrent.Executors
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey,
                                       private val createProfileSaga: CreateProfileSaga) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(TokenService::class.java)
    }

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)
        return StandardToken(
                user.username.value,
                user.id,
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
        val client = iam.apiToken(clientCredentials);

        return StandardToken(
                client.clientName,
                null,
                client.permissions,
                Duration.ofHours(2),
                privateKey = privateKey
        )
    }
}
