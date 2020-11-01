package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory

class ApplicationParametersFactory : Factory<ApplicationParameters> {

    private val applicationParameters: ApplicationParameters = ApplicationParameters(
            FusionAuthConfiguration(
                    serverUrl = System.getenv("fusionServerUrl"),
                    applicationId = System.getenv("fusionApplicationId"),
                    tenantId = System.getenv("fusionTenantId"),
                    apiKey = System.getenv("fusionApiKey")
            )
    )

    override fun provide() = applicationParameters

    override fun dispose(instance: ApplicationParameters?) {
    }
}