package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.api.CompanyCreatedEvent
import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.events.DomainEvent
import java.time.Clock
import java.time.OffsetDateTime

data class Company(
    val id : CompanyId,
    val uuid: CompanyUUID,
    val name: String,
    val phone: PhoneNumber,
    val email: Email,
    val logoUrl: String?,
    val services: Services,
    val createdBy: CompanyRepresentativeUUID,
    val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
    val lastModifiedDate: OffsetDateTime? = null,
    val lastModifiedBy: String? = null,
    val version: Long = 0L
) {
    companion object {
        fun create(
            id: CompanyId,
            name: String,
            email: Email,
            phoneNumber: PhoneNumber,
            services: Services,
            createdBy: CompanyRepresentativeUUID,
            logoUrl: String? = null
        ): ResultWithEvents<Company, DomainEvent> {

            val result = Company(
                id,
                CompanyUUID.random(),
                name,
                phoneNumber,
                email,
                logoUrl,
                services,
                createdBy
            )

            val event = CompanyCreatedEvent(
                id,
                result.uuid,
                name,
                phoneNumber,
                email,
                services,
                createdBy,
                result.createdDate
            )

            return ResultWithEvents(result, listOf(event))
        }
    }
}