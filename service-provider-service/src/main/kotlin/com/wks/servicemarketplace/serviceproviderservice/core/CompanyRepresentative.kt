package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class CompanyRepresentativeId(@JsonValue override val value: Long) : Id<Long>(value)
data class CompanyRepresentativeUUID(@JsonValue override val value: UUID) : Id<UUID>(value) {
    companion object {
        fun random() = CompanyRepresentativeUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = CompanyRepresentativeUUID(UUID.fromString(uuidString))
    }
    override fun toString() = value.toString()
}

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