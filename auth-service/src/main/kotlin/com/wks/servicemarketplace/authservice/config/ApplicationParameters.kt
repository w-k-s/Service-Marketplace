package com.wks.servicemarketplace.authservice.config

data class ApplicationParameters(
        val fusionAuthConfiguration: FusionAuthConfiguration,
        val amqpHost: String,
        val amqpPort: Int,
        val clientId: String,
        val clientSecret: String
)