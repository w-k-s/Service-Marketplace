package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.common.*
import java.time.OffsetDateTime
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateCompanyResponse(
        val id: CompanyId,
        val uuid: CompanyUUID,
        val name: String,
        val phone: PhoneNumber,
        val email: Email,
        val createdBy: CompanyRepresentativeUUID,
        val createdDate: OffsetDateTime,
        val lastModifiedBy: String? = null,
        val lastModifiedDate: OffsetDateTime? = null,
        val version: Long
)

data class CreateCompanyRequest(
        @NotBlank
        val name: String,
        @NotNull
        val phone: PhoneNumber,
        @NotNull
        val email: Email,
        val logoUrl: String?,
        @NotEmpty
        @NotNull
        val services: Services,
        val correlationId: String?
)

data class CreateCompanyRepresentativeResponse(
        private val id: CompanyRepresentativeId,
        private val uuid: CompanyRepresentativeUUID
)

data class CreateCompanyRepresentativeRequest constructor(
        @NotNull
        val uuid: UUID,
        @NotNull
        val name: Name,
        @NotNull
        val email: Email,
        @NotNull
        val phoneNumber: PhoneNumber,
)

fun Company.toCompanyProtocolBuffer(): com.wks.servicemarketplace.api.proto.Company {
    val builder = com.wks.servicemarketplace.api.proto.Company.newBuilder()
            .setId(this.id.value.toInt())
            .setUuid(this.uuid.toString())
            .setEmail(this.email.value)
            .setPhone(this.phone.value)
            .setLogoUrl(this.logoUrl)
            .setCreatedBy(this.createdBy.toString())
            .setLastModifiedBy(this.lastModifiedBy)
            .setVersion(this.version.toInt())

    this.services.forEachIndexed { index, service ->
        builder.setServices(index, service.code)
    }
    return builder.build()
}

