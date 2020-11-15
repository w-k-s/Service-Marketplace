package com.wks.servicemarketplace.serviceproviderservice.config

data class ApplicationParameters(
        val amqpHost: String,
        val amqpPort: Int,
        val clientId: String,
        val clientSecret: String
)