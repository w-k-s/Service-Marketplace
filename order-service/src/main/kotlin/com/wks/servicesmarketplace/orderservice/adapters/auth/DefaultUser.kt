package com.wks.servicesmarketplace.orderservice.adapters.auth

import com.wks.servicesmarketplace.orderservice.core.auth.User


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