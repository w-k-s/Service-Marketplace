package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class AddressId(@JsonValue override val value: Long) : Id<Long>(value)
data class AddressUUID(@JsonValue override val value: UUID) : Id<UUID>(value) {
    companion object {
        fun random() = AddressUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = AddressUUID(UUID.fromString(uuidString))
    }
}

data class Address(
        val id: Long,
        val externalId: AddressId,
        val uuid: AddressUUID,
        val companyId: CompanyId,
        val name: String,
        val line1: String,
        val line2: String,
        val city: String,
        val countryCode: CountryCode,
        val latitude: BigDecimal,
        val longitude: BigDecimal,
        val createdBy: String,
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val lastModifiedBy: String? = null,
        val lastModifiedDate: OffsetDateTime? = null,
        val version: Long = 0L
)