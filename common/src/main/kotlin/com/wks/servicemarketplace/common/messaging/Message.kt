package com.wks.servicemarketplace.common.messaging

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
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

@JsonDeserialize(builder = Message.Builder::class)
data class Message private constructor(
        @JsonProperty("id") val id: MessageId,
        @JsonProperty("type") val type: String,
        @JsonProperty("payload") val payload: String,
        @JsonProperty("destinationExchange") val destinationExchange: String,
        @JsonProperty("published") val published: Boolean,
        @JsonProperty("correlationId") val correlationId: String?,
        @JsonProperty("destinationRoutingKey") val destinationRoutingKey: String?,
        @JsonProperty("destinationQueue") val destinationQueue: String?,
        @JsonProperty("replyExchange") val replyExchange: String?,
        @JsonProperty("replyRoutingKey") val replyRoutingKey: String?,
        @JsonProperty("replyQueue") val replyQueue: String?,
        @JsonProperty("deadLetterExchange") val deadLetterExchange: String?,
        @JsonProperty("deadLetterRoutingKey") val deadLetterRoutingKey: String?,
        @JsonProperty("deadLetterQueue") val deadLetterQueue: String?
) {

    companion object {
        @JvmStatic
        fun builder(messageId: MessageId,
                    type: String,
                    payload: String,
                    destinationExchange: String) = Builder(messageId, type, payload, destinationExchange)
    }


    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    data class Builder @JsonCreator constructor(@JsonProperty("id") val messageId: MessageId,
                                                @JsonProperty("type") val type: String,
                                                @JsonProperty("payload") val payload: String,
                                                @JsonProperty("destinationExchange") val destinationExchange: String) {

        var correlationId: String? = null
        var published: Boolean = false

        var destinationRoutingKey: String? = null
        var destinationQueue: String? = null
        var replyExchange: String? = null
        var replyRoutingKey: String? = null
        var replyQueue: String? = null
        var deadLetterExchange: String? = null
        var deadLetterRoutingKey: String? = null
        var deadLetterQueue: String? = null

        fun withCorrelationId(correlationId: String?) = apply { this.correlationId = correlationId }
        fun withPublished(published: Boolean?) = apply { this.published = published ?: false }

        fun withDestinationRoutingKey(destinationRoutingKey: String?) = apply { this.destinationRoutingKey = destinationRoutingKey }
        fun withDestinationQueue(destinationQueue: String?) = apply { this.destinationQueue = destinationQueue }

        fun withReplyExchange(replyExchange: String?) = apply { this.replyExchange = replyExchange }
        fun withReplyRoutingKey(replyRoutingKey: String?) = apply { this.replyRoutingKey = replyRoutingKey }
        fun withReplyQueue(replyQueue: String?) = apply { this.replyQueue = replyQueue }

        fun withDeadLetterExchange(deadLetterExchange: String?) = apply { this.deadLetterExchange = deadLetterExchange }
        fun withDeadLetterRoutingKey(deadLetterRoutingKey: String?) = apply { this.deadLetterRoutingKey = deadLetterRoutingKey }
        fun withDeadLetterQueue(deadLetterQueue: String?) = apply { this.deadLetterQueue = deadLetterQueue }

        fun build() = Message(
                id = this.messageId,
                type = this.type,
                payload =  this.payload,
                destinationExchange =  this.destinationExchange,
                published = this.published,
                correlationId = this.correlationId,
                destinationRoutingKey = this.destinationRoutingKey,
                destinationQueue = this.destinationQueue,
                replyExchange = this.replyExchange,
                replyRoutingKey = this.replyRoutingKey,
                replyQueue = this.replyQueue,
                deadLetterExchange = this.deadLetterExchange,
                deadLetterRoutingKey = this.deadLetterRoutingKey,
                deadLetterQueue = this.deadLetterQueue
        )
    }
}