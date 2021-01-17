package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.common.AddressId
import com.wks.servicemarketplace.common.AddressUUID
import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.CountryCode
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime

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