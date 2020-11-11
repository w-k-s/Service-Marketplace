package com.wks.servicemarketplace.authservice.core.events

import java.io.IOException

interface EventPublisher {
    @Throws(IOException::class)
    fun customerAccountCreated(token: String, event: AccountCreatedEvent)
    fun serviceProviderAccountCreated(token: String, event: AccountCreatedEvent)
}