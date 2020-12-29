package com.wks.servicemarketplace.authservice.core

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.authservice.core.utils.ModelValidator
import java.util.*
import javax.validation.constraints.NotNull

interface User {
    val id: UserId
    val name: Name
    val username: Email
    val email: Email
    val mobileNumber: PhoneNumber
    val role: UserRole
    val type: UserType
    val permissions: List<String>
}

data class UserId private constructor(@NotNull @JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = ModelValidator.validate(UserId(uuid))

        @JvmStatic
        fun fromString(uuidString: String) = UserId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = UserId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

enum class UserRole(val code: String) {
    CUSTOMER("Customer"),
    SERVICE_PROVIDER("ServiceProvider"),
    COMPANY_REPRESENTATIVE("CompanyRepresentative");

    companion object {
        fun of(role: String) = values().first { it.code == role }
    }
}

enum class UserType(val code: String) {
    CUSTOMER("Customer"),
    SERVICE_PROVIDER("ServiceProvider");

    companion object {
        fun of(code: String): UserType {
            return when (code) {
                CUSTOMER.code -> CUSTOMER
                SERVICE_PROVIDER.code -> SERVICE_PROVIDER
                else -> throw IllegalArgumentException("Unknown user type code '$code'")
            }
        }
    }
}