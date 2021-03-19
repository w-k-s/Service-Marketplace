package com.wks.servicemarketplace.authservice.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.events.DomainEvent
import com.wks.servicemarketplace.common.events.EventType

data class AccountCreatedEvent @JsonCreator constructor(
        @JsonProperty("uuid") val uuid: UserId,
        @JsonProperty("username") val username: Email,
        @JsonProperty("name") val name: Name,
        @JsonProperty("email") val email: Email,
        @JsonProperty("mobileNumber") val mobileNumber: PhoneNumber,
        @JsonProperty("type") val type: UserType
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