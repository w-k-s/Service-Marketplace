package com.wks.servicemarketplace.common.auth

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.errors.CoreException
import java.security.Principal

interface Authentication : Principal {
    val userId: UserId?
    val permissions: Permissions
    fun hasRole(role: Permission): Boolean
    fun checkRole(role: Permission)
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

class DefaultAuthentication(override val userId: UserId?,
                            private val name: String,
                            override val permissions: Permissions) : Authentication {
    override fun hasRole(role: Permission) = permissions.contains(role)

    override fun checkRole(role: Permission) {
        if (!hasRole(role)) {
            throw CoreException.unauthorized(role)
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