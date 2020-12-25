package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory

class ApplicationParametersFactory : Factory<ApplicationParameters> {

    private val applicationParameters: ApplicationParameters = ApplicationParameters.Builder().also {
        it.fusionAuthConfiguration = FusionAuthConfiguration(
                serverUrl = System.getenv("fusionServerUrl"),
                applicationId = System.getenv("fusionApplicationId"),
                tenantId = System.getenv("fusionTenantId"),
                apiKey = System.getenv("fusionApiKey")
        )
        it.jdbcUrl = System.getenv("jdbcUrl")
        it.jdbcUsername = System.getenv("jdbcUsername")
        it.jdbcPassword = System.getenv("jdbcPassword")
        it.amqpHost = System.getenv("amqpHost")
        it.amqpPort = System.getenv("amqpPort").toInt()
        it.amqpPrefetchCount = System.getenv("amqpPrefetchCount")?.toIntOrNull()
        it.clientId = System.getenv("clientId")
        it.clientSecret = System.getenv("clientSecret")
        it.retryAssignGroupIntervalMinutes = System.getenv("retryAssignGroupIntervalMinutes")?.toLongOrNull()
    }.build()

    override fun provide() = applicationParameters

    override fun dispose(instance: ApplicationParameters?) {
    }
}