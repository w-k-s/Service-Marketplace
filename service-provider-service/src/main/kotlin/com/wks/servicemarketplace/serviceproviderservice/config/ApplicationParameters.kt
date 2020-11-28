package com.wks.servicemarketplace.serviceproviderservice.config

data class ApplicationParameters(
        val amqpHost: String,
        val amqpPort: Int,
        val jdbcUrl: String,
        val jdbcUsername: String,
        val jdbcPassword: String,
        val clientId: String,
        val clientSecret: String
)