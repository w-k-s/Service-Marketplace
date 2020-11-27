package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import com.wks.servicemarketplace.serviceproviderservice.core.auth.User
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.UnauthorizedException

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