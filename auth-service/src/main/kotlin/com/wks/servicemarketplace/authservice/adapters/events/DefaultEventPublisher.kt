package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.authservice.core.events.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class DefaultEventPublisher @Inject constructor(private val channel: Channel,
                                                     private val objectMapper: ObjectMapper) : EventPublisher {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventPublisher::class.java)
    }

    init {
        channel.exchangeDeclare(Exchange.CUSTOMER, BuiltinExchangeType.TOPIC, Durable.TRUE, AutoDelete.FALSE, Internal.FALSE, emptyMap())
        channel.exchangeDeclare(Exchange.SERVICE_PROVIDER, BuiltinExchangeType.TOPIC, Durable.TRUE, AutoDelete.FALSE, Internal.FALSE, emptyMap())
    }

    override fun publish(token: String, event: EventEnvelope): Boolean {
        channel.basicPublish(
                event.exchange(),
                event.routingKey(),
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .correlationId(event.entityId)
                        .headers(mapOf(
                                "Authorization" to token
                        ))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
        // Consider publisher confirms.
        return true
    }

    private fun EventEnvelope.exchange(): String {
        return when (eventType) {
            EventType.CUSTOMER_ACCOUNT_CREATED -> Exchange.CUSTOMER
            EventType.SERVICE_PROVIDER_ACCOUNT_CREATED -> Exchange.SERVICE_PROVIDER
        }
    }

    private fun EventEnvelope.routingKey(): String {
        return when (eventType) {
            EventType.CUSTOMER_ACCOUNT_CREATED -> Outgoing.RoutingKey.CUSTOMER_CREATED
            EventType.SERVICE_PROVIDER_ACCOUNT_CREATED -> Outgoing.RoutingKey.SERVICE_PROVIDER_CREATED
        }
    }
}