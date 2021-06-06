package com.wks.servicemarketplace.api

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.DomainEvent
import com.wks.servicemarketplace.common.events.EventType
import com.wks.servicemarketplace.common.events.FailureEvent
import java.time.OffsetDateTime

class CompanyCreatedEvent constructor(
        val externalId: CompanyId,
        val uuid: CompanyUUID,
        val name: String,
        val phone: PhoneNumber,
        val email: Email,
        val services: Services,
        val createdBy: CompanyRepresentativeUUID,
        val createdDate: OffsetDateTime,
        val version: Long = 0L,
) : DomainEvent {
    override val eventType = EventType.COMPANY_CREATED
    override val entityType = "Company"
}

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

data class ServiceProviderCreationFailedEvent constructor(
        override val errorType: ErrorType,
        override val description: String?
) : FailureEvent {
    override val entityType = "ServiceProvider"
    override val eventType = EventType.SERVICE_PROVIDER_PROFILE_CREATION_FAILED
}