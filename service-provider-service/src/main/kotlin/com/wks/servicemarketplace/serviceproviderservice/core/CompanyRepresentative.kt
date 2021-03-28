package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.api.ServiceProviderCreatedEvent
import java.time.Clock
import java.time.OffsetDateTime


/**
 * The temporary user that creates a company.
 * Once company is created, this user is converted to a admin employee of company.
 * After this, company representative is deleted.
 */
data class CompanyRepresentative(
    val id: Long,
    val externalId: CompanyRepresentativeId,
    val uuid: CompanyRepresentativeUUID,
    val name: Name,
    val email: Email,
    val phone: PhoneNumber,
    val createdBy: String,
    val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
    val lastModifiedBy: String? = null,
    val lastModifiedDate: OffsetDateTime? = null,
    val version: Long = 0L
) {
    companion object {
        fun create(externalId: CompanyRepresentativeId,
                   uuid: CompanyRepresentativeUUID,
                   name: Name,
                   email: Email,
                   phone: PhoneNumber,
                   createdBy: String): ResultWithEvents<CompanyRepresentative, ServiceProviderCreatedEvent> {

            val representative = CompanyRepresentative(
                0,
                externalId,
                uuid,
                name,
                email,
                phone,
                createdBy
            )

            val event = ServiceProviderCreatedEvent(
                externalId,
                uuid,
                name,
                email,
                phone,
                createdBy,
                representative.createdDate,
                representative.version
            )

            return ResultWithEvents(representative, listOf(event))
        }
    }

    fun toEmployee(companyId: CompanyId, createdBy: String): Employee = Employee(
        this.id,
        EmployeeId(this.externalId.value),
        EmployeeUUID(this.uuid.value),
        companyId,
        this.name,
        this.email,
        this.phone,
        createdBy
    )
}