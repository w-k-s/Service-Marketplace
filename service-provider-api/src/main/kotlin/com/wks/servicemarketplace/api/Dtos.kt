package com.wks.servicemarketplace.api

import com.wks.servicemarketplace.common.*
import java.time.Clock
import java.time.OffsetDateTime

data class CompanyResponse(
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
)