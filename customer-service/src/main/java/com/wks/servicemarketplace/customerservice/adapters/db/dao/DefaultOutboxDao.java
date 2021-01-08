package com.wks.servicemarketplace.customerservice.adapters.db.dao;

import com.wks.servicemarketplace.common.messaging.Message;
import com.wks.servicemarketplace.common.messaging.MessageId;
import com.wks.servicemarketplace.customerservice.core.daos.OutboxDao;
import org.jooq.JSON;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class DefaultOutboxDao extends BaseDAO implements OutboxDao {

    @Inject
    public DefaultOutboxDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Boolean saveMessage(Connection connection, Message message) {
        return create(connection).insertInto(
                table("outbox"),
                field("message_uuid"),
                field("message_type"),
                field("payload"),
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
                message.getId().toString(),
                message.getType(),
                JSON.valueOf(message.getPayload()),
                message.getPublished(),
                message.getDestinationExchange(),
                message.getDestinationRoutingKey(),
                message.getDestinationQueue(),
                message.getReplyExchange(),
                message.getReplyRoutingKey(),
                message.getReplyQueue(),
                message.getDeadLetterExchange(),
                message.getDeadLetterRoutingKey(),
                message.getDeadLetterQueue()
        ).execute() == 1;
    }

    @Override
    public List<Message> fetchUnpublishedMessages(Connection connection, int limit) {
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
                .fetch((record) -> new Message(
                        MessageId.fromString(record.get(field("message_uuid"), String.class)),
                        record.get(field("message_type"), String.class),
                        record.get(field("payload"), String.class),
                        record.get(field("destination_exchange"), String.class),
                        record.get(field("published"), Boolean.class),
                        record.get(field("correlation_id"), String.class),
                        record.get(field("destination_routing_key"), String.class),
                        record.get(field("destination_queue"), String.class),
                        record.get(field("reply_exchange"), String.class),
                        record.get(field("reply_routing_key"), String.class),
                        record.get(field("reply_queue"), String.class),
                        record.get(field("dead_letter_exchange"), String.class),
                        record.get(field("dead_letter_routing_key"), String.class),
                        record.get(field("dead_letter_queue"), String.class)
                ));
    }

    @Override
    public boolean setMessagePublished(Connection connection, MessageId messageId) {
        return create(connection).update(table("outbox"))
                .set(field("published"), true)
                .where(field("published").eq(false).and(field("message_uuid").eq(messageId.toString())))
                .execute() == 1;
    }
}
