package com.wks.servicemarketplace.common.messaging

import java.util.*

data class MessageId private constructor(val value: UUID) {
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

data class Message(
        val id: MessageId,
        val type: String,
        val payload: String,
        val destinationExchange: String,
        val published: Boolean = false,
        val correlationId: String? = null,
        val destinationRoutingKey: String? = null,
        val destinationQueue: String? = null,
        val replyExchange: String? = null,
        val replyRoutingKey: String? = null,
        val replyQueue: String? = null,
        val deadLetterExchange: String? = null,
        val deadLetterRoutingKey: String? = null,
        val deadLetterQueue: String? = null
)