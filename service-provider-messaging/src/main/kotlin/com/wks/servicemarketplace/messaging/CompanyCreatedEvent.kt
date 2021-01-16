package com.wks.servicemarketplace.messaging

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.events.DomainEvent
import com.wks.servicemarketplace.common.events.EventType
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