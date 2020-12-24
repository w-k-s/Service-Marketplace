package com.wks.servicemarketplace.authservice.core

import java.lang.IllegalArgumentException

interface User {
    val id: String
    val firstName: String
    val lastName: String
    val username: String
    val email: String
    val mobileNumber: String
    val role: UserRole
    val type: UserType
    val permissions: List<String>
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