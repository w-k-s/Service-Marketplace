package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.wks.servicemarketplace.authservice.core.User

data class FusionAuthUser(
    override val username: String,
    override val role: String,
    override val permissions: List<String>,
    val token: String
) : User