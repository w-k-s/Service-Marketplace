package com.wks.servicemarketplace.authservice.config

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class AmqpChannelFactory @Inject constructor(connection: Connection) : Factory<Channel> {

    private val channel = connection.createChannel()

    override fun provide(): Channel = channel

    override fun dispose(instance: Channel?) {
        channel?.close()
    }
}