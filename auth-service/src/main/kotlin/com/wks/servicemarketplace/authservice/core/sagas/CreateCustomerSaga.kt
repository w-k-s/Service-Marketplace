package com.wks.servicemarketplace.authservice.core.sagas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.OutboxDao
import com.wks.servicemarketplace.authservice.messaging.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging
import com.wks.servicemarketplace.common.errors.CoreRuntimeException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.events.EventId
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import javax.inject.Inject

class CreateCustomerSaga @Inject constructor(private val eventDao: EventDao,
                                             private val outboxDao: OutboxDao,
                                             private val objectMapper: ObjectMapper) {

    fun start(accountCreated: AccountCreatedEvent): EventId {
        eventDao.connection().use { conn ->
            try {
                conn.autoCommit = false

                val eventId = EventId.random()
                val messageId = MessageId.fromString(eventId.toString())
                val payload = objectMapper.writeValueAsString(AccountCreatedEvent::class)

                eventDao.saveEvent(conn, EventEnvelope(
                        eventId,
                        accountCreated.eventType,
                        payload,
                        accountCreated.uuid.toString(),
                        accountCreated.entityType
                ))

                outboxDao.saveMessage(conn, Message(
                        messageId,
                        accountCreated.eventType.name,
                        payload,
                        destinationExchange = AuthMessaging.Exchange.MAIN,
                        destinationRoutingKey = AuthMessaging.RoutingKey.CUSTOMER_ACCOUNT_CREATED,
                        correlationId = accountCreated.uuid.toString()
                ))

                //TODO save state machine.

                conn.commit()

                return eventId
            } catch (e: Exception) {
                conn.rollback()
                throw e
            }
        }
    }
}