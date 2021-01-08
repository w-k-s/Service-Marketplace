package com.wks.servicemarketplace.common.events

import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.CoreThrowable
import com.wks.servicemarketplace.common.errors.ErrorType
import java.util.*

interface DomainEvent {
    val eventType: EventType
    val entityType: String
}

interface FailureEvent : DomainEvent {
    val errorType: ErrorType
    val description: String?
}

open class DefaultFailureEvent(override val eventType: EventType,
                               override val entityType: String,
                               coreException: CoreThrowable) : FailureEvent {
    override val errorType = coreException.errorType
    override val description = coreException.message
}

data class EventEnvelope(
        val eventId: EventId,
        val eventType: EventType,
        val eventBody: String,
        val entityId: String,
        val entityType: String
) {
    constructor(event: DomainEvent, entityId: String, eventBody: String)
            : this(EventId.random(), event.eventType, eventBody, entityId, event.entityType)
}

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
    SERVICE_PROVIDER_ACCOUNT_CREATED,
    ADDRESS_ADDED,
    CUSTOMER_PROFILE_CREATED,
    CUSTOMER_PROFILE_CREATION_FAILED
}