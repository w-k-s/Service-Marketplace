package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties
import com.wks.servicemarketplace.serviceproviderservice.core.CompanyCreatedEvent
import com.wks.servicemarketplace.serviceproviderservice.core.events.EventPublisher
import javax.inject.Inject

data class DefaultEventPublisher @Inject constructor(private val channel: Channel,
                                                     private val objectMapper: ObjectMapper) : EventPublisher {

    init {
        channel.exchangeDeclare(Exchange.SERVICE_PROVIDER, BuiltinExchangeType.TOPIC, true, true, emptyMap())
    }

    override fun companyCreated(token: String, event: CompanyCreatedEvent) {
        channel.queueDeclare(Queue.COMPANY_CREATED, true, false, true, mutableMapOf<String, Any>())

        channel.basicPublish(
                Exchange.SERVICE_PROVIDER,
                RoutingKey.COMPANY_CREATED,
                MessageProperties.PERSISTENT_TEXT_PLAIN.builder()
                        .headers(mapOf("Authorization" to "Bearer $token"))
                        .build(),
                objectMapper.writeValueAsBytes(event)
        )
    }
}