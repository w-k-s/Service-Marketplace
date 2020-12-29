package com.wks.servicemarketplace.authservice.core.events

import java.io.IOException

interface EventPublisher {
    @Throws(IOException::class)
    fun publish(token: String, event: EventEnvelope): Boolean
}