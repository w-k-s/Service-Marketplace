package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.api.ServiceProviderMessaging
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class DefaultMessagePublisher constructor(private val channel: Channel) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultMessagePublisher::class.java)
    }

    init {
        ServiceProviderMessaging.Exchange.MAIN.declare(channel)
    }

    fun publish(token: String, message: Message): Boolean {
        try {
            channel.basicPublish(
                message.destinationExchange,
                message.destinationRoutingKey,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                    .messageId(message.id.toString())
                    .replyTo(message.replyQueue)
                    .correlationId(message.correlationId)
                    .headers(
                        mapOf(
                            "Authorization" to "Bearer $token"
                        )
                    )
                    .build(),
                message.payload.toByteArray()
            )
            // Consider publisher confirms.
            return true
        } catch (e: Exception) {
            LOGGER.error("Failed to publish message '$message;", e)
            return false
        }
    }
}