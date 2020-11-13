package com.wks.servicemarketplace.authservice.core

import java.lang.IllegalArgumentException

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