package com.wks.servicemarketplace.common.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.common.events.EventType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MessageTest {
    @Test
    fun `GIVEN a serialized message WHEN it is deserialized THEN it matches the original message`() {
        // GIVEN
        val messageId = MessageId.random()
        val type = EventType.CUSTOMER_ACCOUNT_CREATED.name
        val payload = """{"hello":"world"}"""
        val correlationId = messageId.toString()
        val destinationExchange = "DESTINATION EXCHANGE"
        val destinationRoutingKey = "DESTINATION ROUTING KEY"

        val objectMapper = ObjectMapper()
        val serializedMessage = objectMapper.writeValueAsString(
                Message.builder(messageId, type, payload, destinationExchange)
                        .withCorrelationId(correlationId)
                        .withDestinationRoutingKey(destinationRoutingKey)
                        .build()
        )

        // WHEN
        objectMapper.readValue(serializedMessage, Message::class.java).let {
            assertThat(it.id).isEqualTo(messageId)
            assertThat(it.type).isEqualTo(type)
            assertThat(it.payload).isEqualTo(payload)
            assertThat(it.correlationId).isEqualTo(correlationId)
            assertThat(it.destinationExchange).isEqualTo(destinationExchange)
            assertThat(it.destinationRoutingKey).isEqualTo(destinationRoutingKey)
        }
    }
}