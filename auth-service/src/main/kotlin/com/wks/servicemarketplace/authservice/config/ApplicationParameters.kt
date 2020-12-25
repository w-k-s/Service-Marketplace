package com.wks.servicemarketplace.authservice.config

data class ApplicationParameters(
        val fusionAuthConfiguration: FusionAuthConfiguration,
        val jdbcUrl: String,
        val jdbcUsername: String,
        val jdbcPassword: String,
        val amqpHost: String,
        val amqpPort: Int,
        val amqpPrefetchCount: Int,
        val clientId: String,
        val clientSecret: String,
        val retryAssignGroupIntervalMinutes: Long
) {

    companion object {
        const val DEFAULT_AMQP_PREFETCH_COUNT = 10
        const val DEFAULT_RETRY_ASSIGN_GROUP_INTERVAL_MINUTES = 5L
        const val MINIMUM_RETRY_ASSIGN_GROUP_INTERVAL_MINUTES = 5L
    }

    class Builder {
        lateinit var fusionAuthConfiguration: FusionAuthConfiguration
        lateinit var jdbcUrl: String
        lateinit var jdbcUsername: String
        lateinit var jdbcPassword: String
        lateinit var amqpHost: String
        var amqpPort: Int? = null
        var amqpPrefetchCount: Int? = null
        lateinit var clientId: String
        lateinit var clientSecret: String
        var retryAssignGroupIntervalMinutes: Long? = null

        fun build() = ApplicationParameters(
                this.fusionAuthConfiguration,
                this.jdbcUrl,
                this.jdbcUsername,
                this.jdbcPassword,
                this.amqpHost,
                this.amqpPort!!,
                this.amqpPrefetchCount ?: DEFAULT_AMQP_PREFETCH_COUNT,
                this.clientId,
                this.clientSecret,
                this.retryAssignGroupIntervalMinutes
                        ?.takeIf { it >= MINIMUM_RETRY_ASSIGN_GROUP_INTERVAL_MINUTES }
                        ?: DEFAULT_RETRY_ASSIGN_GROUP_INTERVAL_MINUTES
        )
    }
}