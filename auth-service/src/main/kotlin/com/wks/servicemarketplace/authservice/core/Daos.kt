package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import java.sql.Connection
import java.sql.SQLException
import java.util.*

interface Dao {
    @Throws(SQLException::class)
    fun connection(): Connection
}

interface EventDao : Dao {
    fun saveEvent(connection: Connection, event: EventEnvelope): Boolean
}

interface OutboxDao : Dao {
    fun saveMessage(connection: Connection, message: Message): Boolean
    fun fetchUnpublishedMessages(connection: Connection): List<Message>
    fun setMessagePublished(connection: Connection, messageId: MessageId): Boolean
}