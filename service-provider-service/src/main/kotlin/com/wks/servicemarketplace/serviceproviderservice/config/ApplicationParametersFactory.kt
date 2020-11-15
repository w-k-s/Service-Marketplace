package com.wks.servicemarketplace.serviceproviderservice.config

import org.glassfish.hk2.api.Factory

class ApplicationParametersFactory : Factory<ApplicationParameters> {

    private val applicationParameters: ApplicationParameters = ApplicationParameters(
            amqpHost = System.getenv("amqpHost"),
            amqpPort = System.getenv("amqpPort").toInt(),
            clientId = System.getenv("clientId"),
            clientSecret = System.getenv("clientSecret")
    )

    override fun provide() = applicationParameters

    override fun dispose(instance: ApplicationParameters?) {
    }
}