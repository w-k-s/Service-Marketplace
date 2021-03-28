package com.wks.servicemarketplace.api

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.events.DomainEvent
import com.wks.servicemarketplace.common.events.EventType
import java.time.OffsetDateTime

data class ServiceProviderCreatedEvent constructor(
    val externalId: CompanyRepresentativeId,
    val uuid: CompanyRepresentativeUUID,
    val name: Name,
    val email: Email,
    val phone: PhoneNumber,
    val createdBy: String,
    val createdDate: OffsetDateTime,
    val version: Long
) : DomainEvent {
    override val entityType = "ServiceProvider"
    override val eventType = EventType.SERVICE_PROVIDER_PROFILE_CREATED
}