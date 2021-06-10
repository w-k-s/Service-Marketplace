package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class EmployeeId(@JsonValue val value: Long)
data class EmployeeUUID(@JsonValue val value: UUID) {
    companion object {
        fun random() = EmployeeUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = EmployeeUUID(UUID.fromString(uuidString))
    }
}

data class Employee(
    val id: EmployeeId,
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