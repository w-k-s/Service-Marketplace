package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.serviceproviderservice.core.utils.ModelValidator
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class EmployeeId(override val value: Long) : Id<Long>(value)
data class EmployeeUUID(override val value: UUID) : Id<UUID>(value) {
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
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val createdBy: String,
        val lastModifiedDate: OffsetDateTime,
        val lastModifiedBy: String,
        val version: Long
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