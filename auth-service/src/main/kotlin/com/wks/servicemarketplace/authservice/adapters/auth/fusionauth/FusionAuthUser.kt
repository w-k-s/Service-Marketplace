package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.wks.servicemarketplace.authservice.core.User

data class FusionAuthUser(
        override val id: String,
        override val firstName: String,
        override val lastName: String,
        override val username: String,
        override val email: String,
        override val role: String,
        override val permissions: List<String>,
        val token: String
) : User