package com.wks.servicemarketplace.authservice.core.sagas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.OutboxDao
import com.wks.servicemarketplace.authservice.core.SagaDao
import com.wks.servicemarketplace.authservice.messaging.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.events.EventId
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.common.sagas.DeadlineAfter
import com.wks.servicemarketplace.common.sagas.Saga
import com.wks.servicemarketplace.common.sagas.TransactionId
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreatedEvent
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreationFailedEvent
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.inject.Inject

enum class CreateProfileState : Saga.State {
    CREATING_PROFILE,
    PROFILE_CREATED;

    override fun stateName() = this.name
}

class CreateProfileSaga @Inject constructor(private val dataSource: DataSource,
                                            private val eventDao: EventDao,
                                            private val outboxDao: OutboxDao,
                                            private val sagaDao: SagaDao,
                                            private val objectMapper: ObjectMapper) {

    companion object {
        val LOGGER = LoggerFactory.getLogger(this::class.java)
        const val SAGA_NAME = "CreateCustomerSaga";
    }

    fun start(accountCreated: AccountCreatedEvent): EventId {
        dataSource.connection().use { conn ->
            try {
                conn.autoCommit = false

                val eventId = EventId.random()
                val messageId = MessageId.fromString(eventId.toString())
                val transactionId = TransactionId.random()
                val payload = objectMapper.writeValueAsString(accountCreated)

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
                        destinationRoutingKey = when (accountCreated.type) {
                            UserType.SERVICE_PROVIDER -> AuthMessaging.RoutingKey.SERVICE_PROVIDER_ACCOUNT_CREATED
                            UserType.CUSTOMER -> AuthMessaging.RoutingKey.CUSTOMER_ACCOUNT_CREATED
                        },
                        correlationId = accountCreated.uuid.toString()
                ))

                sagaDao.saveSaga(conn, Saga(
                        transactionId,
                        SAGA_NAME,
                        accountCreated.uuid.toString(),
                        accountCreated.entityType,
                        CreateProfileState.CREATING_PROFILE,
                        DeadlineAfter(Duration.ofMinutes(1)),
                        eventId
                ))

                conn.commit()

                return eventId
            } catch (e: Exception) {
                LOGGER.error("Failed to create profile ${e.message}", e)
                conn.rollback()
                throw e
            }
        }
    }

    fun on(authentication: Authentication, customerCreatedEvent: CustomerCreatedEvent) {

    }

    fun on(authentication: Authentication, customerCreationFailedEvent: CustomerCreationFailedEvent) {

    }
}