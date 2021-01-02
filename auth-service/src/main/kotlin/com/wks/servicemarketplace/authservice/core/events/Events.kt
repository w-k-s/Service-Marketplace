package com.wks.servicemarketplace.authservice.core.events

import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserType
import java.util.*

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

interface DomainEvent

data class AccountCreatedEvent(
        val uuid: UserId,
        val username: Email,
        val name: Name,
        val email: Email,
        val mobileNumber: PhoneNumber,
        val type: UserType
) : DomainEvent {
    constructor(user: User) : this(
            user.id,
            user.username,
            user.name,
            user.email,
            user.mobileNumber,
            user.type
    )
}