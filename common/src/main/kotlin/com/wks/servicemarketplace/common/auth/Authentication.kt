package com.wks.servicemarketplace.common.auth

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.errors.UnauthorizedException
import java.security.Principal
import kotlin.jvm.Throws

interface Authentication : Principal {
    val user: User?
    val token: String
    fun hasRole(role: String): Boolean
    fun checkRole(role: String)
}

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

class DefaultAuthentication(override val user: User?,
                            override val token: String,
                            private val name: String,
                            private val permissions: List<String>) : Authentication {
    override fun hasRole(role: String) = permissions.contains(role)

    override fun checkRole(role: String) {
        if (!hasRole(role)) {
            throw UnauthorizedException(message = "User does not have role '$role'")
        }
    }

    override fun getName() = name
}

data class DefaultUser(
        override val id: UserId,
        override val name: Name,
        override val username: Email,
        override val email: Email,
        override val mobileNumber: PhoneNumber,
        override val role: UserRole,
        override val type: UserType,
        override val permissions: List<String>
) : User, Principal {
    override fun getName() = username.value
}