package com.wks.servicemarketplace.authservice.config

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.wks.servicemarketplace.authservice.adapters.events.AutoDelete
import com.wks.servicemarketplace.authservice.adapters.events.Durable
import com.wks.servicemarketplace.authservice.adapters.events.Exchange
import com.wks.servicemarketplace.authservice.adapters.events.Internal
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class AmqpChannelFactory @Inject constructor(connection: Connection,
                                             parameters: ApplicationParameters) : Factory<Channel> {

    private val channel = connection.createChannel().also {
        it.basicQos(parameters.amqpPrefetchCount)
    }

    init {
        channel.exchangeDeclare(
                AuthMessaging.Exchange.MAIN,
                BuiltinExchangeType.TOPIC,
                Durable.TRUE,
                AutoDelete.FALSE,
                Internal.FALSE,
                emptyMap()
        )
    }

    override fun provide(): Channel = channel

    override fun dispose(instance: Channel?) {
        channel?.close()
    }
}