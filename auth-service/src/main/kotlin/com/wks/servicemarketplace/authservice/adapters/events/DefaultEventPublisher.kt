package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class DefaultEventPublisher @Inject constructor(private val channel: Channel,
                                                     private val objectMapper: ObjectMapper) : EventPublisher {

    companion object{
        private val LOGGER : Logger = LoggerFactory.getLogger(DefaultEventPublisher::class.java)
    }

    init {
        channel.exchangeDeclare(Exchange.ACCOUNT, BuiltinExchangeType.TOPIC, true, true, false, emptyMap())
    }

    override fun customerAccountCreated(token: String, event: AccountCreatedEvent) {
        LOGGER.info("Publishing Customer Account Created: '$event")

        channel.basicPublish(
                Exchange.ACCOUNT,
                Outgoing.RoutingKey.CUSTOMER_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }

    override fun serviceProviderAccountCreated(token: String, event: AccountCreatedEvent) {
        LOGGER.info("Publishing Service Provider Account Created: '$event")

        channel.basicPublish(
                Exchange.ACCOUNT,
                Outgoing.RoutingKey.SERVICE_PROVIDER_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }
}