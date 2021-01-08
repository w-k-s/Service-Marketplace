package com.wks.servicemarketplace.customerservice.core.daos;

import com.wks.servicemarketplace.common.messaging.Message;
import com.wks.servicemarketplace.common.messaging.MessageId;

import java.sql.Connection;
import java.util.List;

public interface OutboxDao extends Dao {
    Boolean saveMessage(Connection connection, Message message);

    List<Message> fetchUnpublishedMessages(Connection connection, int limit);

    boolean setMessagePublished(Connection connection, MessageId messageId);
}
