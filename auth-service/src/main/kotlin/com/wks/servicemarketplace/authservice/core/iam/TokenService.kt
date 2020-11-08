package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import java.security.PrivateKey
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey,
                                       private val eventPublisher: EventPublisher) {

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)
        return StandardToken(
                user.username,
                StandardToken.User(user.id, user.firstName, user.lastName, user.username, user.email, user.role),
                user.permissions,
                Duration.of(1, ChronoUnit.HOURS),
                privateKey = privateKey
        )
    }

    fun register(registration: Registration): Identity {
        val identity = iam.register(registration)

        when (identity.type) {
            UserType.CUSTOMER -> eventPublisher.customerAccountCreated(AccountCreatedEvent(identity))
            UserType.SERVICE_PROVIDER -> eventPublisher.serviceProviderAccountCreated(AccountCreatedEvent(identity))
        }

        return identity
    }
}
