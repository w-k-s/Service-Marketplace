package com.wks.servicemarketplace.common.events

import java.util.*

interface DomainEvent {
    val eventType: EventType
    val entityType: String
}

data class EventEnvelope(
        val eventId: EventId,
        val eventType: EventType,
        val eventBody: String,
        val entityId: String,
        val entityType: String
)

data class EventId private constructor(val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = EventId(uuid)

        @JvmStatic
        fun fromString(uuidString: String) = EventId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = EventId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

enum class EventType {
    CUSTOMER_ACCOUNT_CREATED,
    SERVICE_PROVIDER_ACCOUNT_CREATED
}