package com.wks.servicemarketplace.serviceproviderservice.core

import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class CompanyRepresentativeId(override val value: Long) : Id<Long>(value)
data class CompanyRepresentativeUUID(override val value: UUID) : Id<UUID>(value) {
    companion object {
        fun random() = CompanyRepresentativeUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = CompanyRepresentativeUUID(UUID.fromString(uuidString))
    }
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
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val createdBy: String,
        val lastModifiedDate: OffsetDateTime? = null,
        val lastModifiedBy: String? = null,
        val version: Long = 0L
)