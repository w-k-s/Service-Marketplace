package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.serviceproviderservice.core.OutboxDao
import org.jooq.JSON
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.sql.Connection
import javax.inject.Inject

class DefaultOutboxDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), OutboxDao {

    override fun saveMessage(connection: Connection, message: Message): Boolean {
        return create(connection).insertInto(
                table("outbox"),
                field("message_uuid"),
                field("message_type"),
                field("payload"),
                field("correlation_id"),
                field("published"),
                field("destination_exchange"),
                field("destination_routing_key"),
                field("destination_queue"),
                field("reply_exchange"),
                field("reply_routing_key"),
                field("reply_queue"),
                field("dead_letter_exchange"),
                field("dead_letter_routing_key"),
                field("dead_letter_queue")
        ).values(
                message.id.toString(),
                message.type,
                JSON.valueOf(message.payload),
                message.correlationId,
                message.published,
                message.destinationExchange,
                message.destinationRoutingKey,
                message.destinationQueue,
                message.replyExchange,
                message.replyRoutingKey,
                message.replyQueue,
                message.deadLetterExchange,
                message.deadLetterRoutingKey,
                message.deadLetterQueue
        ).execute() == 1
    }

    override fun fetchUnpublishedMessages(connection: Connection): List<Message> {
        return create(connection).select(
                field("message_uuid"),
                field("message_type"),
                field("payload"),
                field("published"),
                field("correlation_id"),
                field("destination_exchange"),
                field("destination_routing_key"),
                field("destination_queue"),
                field("reply_exchange"),
                field("reply_routing_key"),
                field("reply_queue"),
                field("dead_letter_exchange"),
                field("dead_letter_routing_key"),
                field("dead_letter_queue")
        ).from(table("outbox")).where(field("published").eq(false))
                .fetch {
                    Message(
                            MessageId.fromString(it.get(field("message_uuid"), String::class.java)),
                            it.get(field("message_type"), String::class.java),
                            it.get(field("payload"), String::class.java),
                            it.get(field("destination_exchange"), String::class.java),
                            it.get(field("published"), Boolean::class.java),
                            it.get(field("correlation_id"), String::class.java),
                            it.get(field("destination_routing_key"), String::class.java),
                            it.get(field("destination_queue"), String::class.java),
                            it.get(field("reply_exchange"), String::class.java),
                            it.get(field("reply_routing_key"), String::class.java),
                            it.get(field("reply_queue"), String::class.java),
                            it.get(field("dead_letter_exchange"), String::class.java),
                            it.get(field("dead_letter_routing_key"), String::class.java),
                            it.get(field("dead_letter_queue"), String::class.java)
                    )
                }
    }

    override fun setMessagePublished(connection: Connection, messageId: MessageId): Boolean {
        return create(connection).update(table("outbox"))
                .set(field("published"), true)
                .where(field("published").eq(false).and(field("message_uuid").eq(messageId.toString())))
                .execute() == 1
    }
}