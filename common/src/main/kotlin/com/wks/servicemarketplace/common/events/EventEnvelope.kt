package com.wks.servicemarketplace.common.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.common.errors.CoreException
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

open class DefaultFailureEvent @JsonCreator constructor(
    @JsonProperty("eventType")
    override val eventType: EventType,
    @JsonProperty("entityType")
    override val entityType: String,
    @JsonProperty("errorType")
    override val errorType: ErrorType,
    @JsonProperty("description")
    override val description: String
) : FailureEvent {
    companion object {
        fun fromCoreException(eventType: EventType, entityType: String, exception: CoreException) : FailureEvent{
            return DefaultFailureEvent(eventType, entityType, exception.errorType, exception.message ?:"")
        }
    }
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
    CUSTOMER_PROFILE_CREATION_FAILED,
    SERVICE_PROVIDER_PROFILE_CREATED,
    SERVICE_PROVIDER_PROFILE_CREATION_FAILED,
    COMPANY_CREATED
}