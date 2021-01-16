package com.wks.servicemarketplace.serviceproviderservice.config

data class ApplicationParameters(
    val amqpHost: String,
    val amqpPort: Int,
    val jdbcUrl: String,
    val jdbcUsername: String,
    val jdbcPassword: String,
    val clientId: String,
    val clientSecret: String,
    val authServiceBaseUrl: String,
    val outboxIntervalMillis: Long = DEFAULT_OUTBOX_INTERVAL_MILLIS
) {
    companion object {
        const val DEFAULT_OUTBOX_INTERVAL_MILLIS: Long = 300
    }
}