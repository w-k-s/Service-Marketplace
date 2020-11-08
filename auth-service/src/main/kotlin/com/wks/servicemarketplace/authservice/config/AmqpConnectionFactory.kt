package com.wks.servicemarketplace.authservice.config

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class AmqpConnectionFactory @Inject constructor(applicationParameters: ApplicationParameters) : Factory<Connection> {

    private val connection: Connection = ConnectionFactory().also {
        it.host = applicationParameters.amqpHost
        it.port = applicationParameters.amqpPort
    }.newConnection()

    override fun provide() = connection

    override fun dispose(instance: Connection?) {
        instance?.close()
    }
}