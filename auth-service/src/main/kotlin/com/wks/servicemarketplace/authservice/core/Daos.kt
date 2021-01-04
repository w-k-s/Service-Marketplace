package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.common.sagas.Saga
import com.wks.servicemarketplace.common.sagas.TransactionId
import java.sql.Connection
import java.sql.SQLException

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

interface SagaDao : Dao {
    fun saveSaga(connection: Connection, saga: Saga): Boolean
    fun updateSaga(connection: Connection, transaction: TransactionId, updatedSaga: Saga, oldState: Saga.State): Boolean
}