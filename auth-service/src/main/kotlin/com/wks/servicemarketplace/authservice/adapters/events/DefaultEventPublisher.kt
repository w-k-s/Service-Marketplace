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

    private val EXCHANGE_NAME = "com.wks.servicemarketplace.account.exchange"

    companion object RoutingKey {
        const val CUSTOMER_ACCOUNT_CREATED = "com.wks.servicemarketplace.account.customer.created"
        const val SERVICE_PROVIDER_ACCOUNT_CREATED = "com.wks.servicemarketplace.account.serviceProvider.created"
    }

    init {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, emptyMap())
    }

    override fun customerAccountCreated(token: String, event: AccountCreatedEvent) {
        channel.queueDeclare(CUSTOMER_ACCOUNT_CREATED, true, false, true, mutableMapOf<String, Any>())

        channel.basicPublish(
                EXCHANGE_NAME,
                CUSTOMER_ACCOUNT_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }

    override fun serviceProviderAccountCreated(token: String, event: AccountCreatedEvent) {
        channel.queueDeclare(SERVICE_PROVIDER_ACCOUNT_CREATED, true, false, true, mutableMapOf<String, Any>())

        channel.basicPublish(
                EXCHANGE_NAME,
                SERVICE_PROVIDER_ACCOUNT_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }
}