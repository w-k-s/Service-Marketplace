package com.wks.servicemarketplace.authservice.config

data class ApplicationParameters(
        val fusionAuthConfiguration: FusionAuthConfiguration,
        val amqpHost: String,
        val amqpPort: Int,
        val amqpPrefetchCount: Int? = 10,
        val clientId: String,
        val clientSecret: String,
        val retryAssignGroupIntervalMinutes: Long? = 5
)