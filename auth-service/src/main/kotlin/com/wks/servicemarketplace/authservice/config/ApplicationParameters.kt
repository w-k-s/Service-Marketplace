package com.wks.servicemarketplace.authservice.config

data class ApplicationParameters(
        val fusionAuthConfiguration: FusionAuthConfiguration,
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
        lateinit var amqpHost: String
        var amqpPort: Int? = null
        var amqpPrefetchCount: Int? = null
        lateinit var clientId: String
        lateinit var clientSecret: String
        var retryAssignGroupIntervalMinutes: Long? = null

        fun build() = ApplicationParameters(
                this.fusionAuthConfiguration,
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