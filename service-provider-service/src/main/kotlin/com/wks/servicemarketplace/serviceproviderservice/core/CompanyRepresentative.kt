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
    val id: CompanyRepresentativeId,
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
        fun create(id: CompanyRepresentativeId,
                   uuid: CompanyRepresentativeUUID,
                   name: Name,
                   email: Email,
                   phone: PhoneNumber,
                   createdBy: String): ResultWithEvents<CompanyRepresentative, ServiceProviderCreatedEvent> {

            val representative = CompanyRepresentative(
                id,
                uuid,
                name,
                email,
                phone,
                createdBy
            )

            val event = ServiceProviderCreatedEvent(
                id,
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
        EmployeeId(this.id.value),
        EmployeeUUID(this.uuid.value),
        companyId,
        this.name,
        this.email,
        this.phone,
        createdBy
    )
}