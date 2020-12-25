package com.wks.servicemarketplace.authservice.core.events

import com.wks.servicemarketplace.authservice.core.*

data class EventEnvelope(
        val eventId: String,
        val eventType: String,
        val eventBody: String,
        val entityId: String,
        val entityType: String
)

interface DomainEvent

data class AccountCreatedEvent(
        val uuid: UserId,
        val username: Email,
        val name: Name,
        val email: Email,
        val mobileNumber: PhoneNumber
) : DomainEvent {
    constructor(user: User) : this(
            user.id,
            user.username,
            user.name,
            user.email,
            user.mobileNumber
    )
}