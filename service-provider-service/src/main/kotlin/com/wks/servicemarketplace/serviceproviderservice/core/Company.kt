package com.wks.servicemarketplace.serviceproviderservice.core

import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class CompanyId(override val value: Long) : Id<Long>(value)
data class CompanyUUID(override val value: UUID) : Id<UUID>(value) {
    companion object {
        fun random() = CompanyUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = CompanyUUID(UUID.fromString(uuidString))
    }
}

data class Company(
        val id: Long = 0L,
        val externalId: CompanyId,
        val uuid: CompanyUUID,
        val name: String,
        val phone: PhoneNumber,
        val email: Email,
        val logoUrl: String,
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val createdBy: String,
        val lastModifiedDate: OffsetDateTime? = null,
        val lastModifiedBy: String? = null,
        val version: Long = 0L
)