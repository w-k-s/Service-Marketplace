package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.authservice.core.events.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import javax.inject.Inject

data class DefaultEventPublisher @Inject constructor(private val channel: Channel,
                                                     private val objectMapper: ObjectMapper) : EventPublisher {

    init {
        channel.exchangeDeclare(Exchange.ACCOUNT, BuiltinExchangeType.TOPIC, true, true, emptyMap())
    }

    override fun customerAccountCreated(token: String, event: AccountCreatedEvent) {
        channel.queueDeclare(Queue.CUSTOMER_CREATED, true, false, true, mutableMapOf<String, Any>())

        channel.basicPublish(
                Exchange.ACCOUNT,
                RoutingKey.CUSTOMER_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }

    override fun serviceProviderAccountCreated(token: String, event: AccountCreatedEvent) {
        channel.queueDeclare(Queue.SERVICE_PROVIDER_CREATED, true, false, true, mutableMapOf<String, Any>())

        channel.basicPublish(
                Exchange.ACCOUNT,
                RoutingKey.SERVICE_PROVIDER_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }
}