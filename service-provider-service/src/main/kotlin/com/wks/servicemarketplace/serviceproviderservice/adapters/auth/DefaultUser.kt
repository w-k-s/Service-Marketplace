package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.serviceproviderservice.core.auth.User

data class DefaultUser(
        override val id: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val email: String,
        val role: String
) : User {
    override fun getName() = username
}