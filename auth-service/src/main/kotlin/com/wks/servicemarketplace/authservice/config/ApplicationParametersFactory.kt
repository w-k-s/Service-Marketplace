package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory

class ApplicationParametersFactory : Factory<ApplicationParameters> {

    private val applicationParameters: ApplicationParameters = ApplicationParameters(
            FusionAuthConfiguration(
                    serverUrl = System.getenv("fusionServerUrl"),
                    applicationId = System.getenv("fusionApplicationId"),
                    tenantId = System.getenv("fusionTenantId"),
                    apiKey = System.getenv("fusionApiKey")
            ),
            amqpHost = System.getenv("amqpHost"),
            amqpPort = System.getenv("amqpPort").toInt(),
            amqpPrefetchCount = System.getenv("amqpPrefetchCount").toIntOrNull(),
            clientId = System.getenv("clientId"),
            clientSecret = System.getenv("clientSecret"),
            retryAssignGroupIntervalMinutes = System.getenv("retryAssignGroupIntervalMinutes").toLongOrNull()
    )

    override fun provide() = applicationParameters

    override fun dispose(instance: ApplicationParameters?) {
    }
}