package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.serviceproviderservice.core.utils.ModelValidator
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class EmployeeId(@JsonValue override val value: Long) : Id<Long>(value)
data class EmployeeUUID(@JsonValue override val value: UUID) : Id<UUID>(value) {
    companion object {
        fun random() = EmployeeUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = EmployeeUUID(UUID.fromString(uuidString))
    }
}

data class Employee(
        val id: Long,
        val externalId: EmployeeId,
        val uuid: EmployeeUUID,
        val companyId: CompanyId,
        val name: Name,
        val email: Email,
        val phone: PhoneNumber,
        val createdBy: String,
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val lastModifiedBy: String? = null,
        val lastModifiedDate: OffsetDateTime? = null,
        val version: Long = 0L
)

data class Name internal constructor(
        @NotBlank
        @Size(min = 2, max = 50)
        val first: String,
        @NotBlank
        @Size(min = 2, max = 50)
        val last: String
) {
    companion object {
        fun of(first: String, last: String) = ModelValidator.validate(Name(first, last))
    }

    override fun toString() = "$first $last"
}