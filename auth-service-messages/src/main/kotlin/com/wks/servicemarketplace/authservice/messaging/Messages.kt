package com.wks.servicemarketplace.authservice.messaging

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.events.DomainEvent
import com.wks.servicemarketplace.common.events.EventType

data class AccountCreatedEvent(
        val uuid: UserId,
        val username: Email,
        val name: Name,
        val email: Email,
        val mobileNumber: PhoneNumber,
        val type: UserType
) : DomainEvent {

    override val eventType = when (this.type) {
        UserType.CUSTOMER -> EventType.CUSTOMER_ACCOUNT_CREATED
        UserType.SERVICE_PROVIDER -> EventType.SERVICE_PROVIDER_ACCOUNT_CREATED
    }

    override val entityType = this.type.name

    constructor(user: User) : this(
            user.id,
            user.username,
            user.name,
            user.email,
            user.mobileNumber,
            user.type
    )
}