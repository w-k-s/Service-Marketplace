package com.wks.servicemarketplace.authservice.core.events

import java.io.IOException

interface EventPublisher {
    @Throws(IOException::class)
    fun customerAccountCreated(event: AccountCreatedEvent)
    fun serviceProviderAccountCreated(event: AccountCreatedEvent)
}