package com.wks.servicemarketplace.common.messaging

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

data class MessageId private constructor(@JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = MessageId(uuid)

        @JvmStatic
        fun fromString(uuidString: String) = MessageId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = MessageId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

data class Message @JsonCreator constructor(
        @JsonProperty("id") val id: MessageId,
        @JsonProperty("type") val type: String,
        @JsonProperty("payload") val payload: String,
        @JsonProperty("destinationExchange") val destinationExchange: String,
        @JsonProperty("published") val published: Boolean = false,
        @JsonProperty("correlationId") val correlationId: String? = null,
        @JsonProperty("destinationRoutingKey") val destinationRoutingKey: String? = null,
        @JsonProperty("destinationQueue") val destinationQueue: String? = null,
        @JsonProperty("replyExchange") val replyExchange: String? = null,
        @JsonProperty("replyRoutingKey") val replyRoutingKey: String? = null,
        @JsonProperty("replyQueue") val replyQueue: String? = null,
        @JsonProperty("deadLetterExchange") val deadLetterExchange: String? = null,
        @JsonProperty("deadLetterRoutingKey") val deadLetterRoutingKey: String? = null,
        @JsonProperty("deadLetterQueue") val deadLetterQueue: String? = null
)