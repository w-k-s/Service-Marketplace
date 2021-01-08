package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.common.messaging.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class DefaultMessagePublisher @Inject constructor(private val channel: Channel,
                                                       private val objectMapper: ObjectMapper) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultMessagePublisher::class.java)
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
                            .headers(mapOf(
                                    "Authorization" to token
                            ))
                            .build(),
                    objectMapper.writeValueAsBytes(message.payload)
            )
            // Consider publisher confirms.
            return true
        } catch (e: Exception) {
            LOGGER.error("Failed to publish message '$message;", e)
            return false
        }
    }
}