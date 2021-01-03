package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.common.messaging.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class DefaultEventPublisher @Inject constructor(private val channel: Channel,
                                                     private val objectMapper: ObjectMapper) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventPublisher::class.java)
    }

    init {
        channel.exchangeDeclare(Exchange.CUSTOMER, BuiltinExchangeType.TOPIC, Durable.TRUE, AutoDelete.FALSE, Internal.FALSE, emptyMap())
        channel.exchangeDeclare(Exchange.SERVICE_PROVIDER, BuiltinExchangeType.TOPIC, Durable.TRUE, AutoDelete.FALSE, Internal.FALSE, emptyMap())
    }

    fun publish(token: String, message: Message): Boolean {
        channel.basicPublish(
                message.destinationExchange,
                message.destinationRoutingKey,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .messageId(message.id.toString())
                        .replyTo(message.replyQueue)
                        .correlationId(message.correlationId)
                        .headers(mapOf(
                                "Authorization" to token
                        ))
                        .build(),
                objectMapper.writeValueAsBytes(message.payload)
        )
        // Consider publisher confirms.
        return true
    }
}