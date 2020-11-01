package com.wks.servicemarketplace.authservice.config

data class FusionAuthConfiguration(
        val serverUrl: String,
        val applicationId: String,
        val tenantId: String,
        val apiKey: String
)