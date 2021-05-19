package com.wks.servicemarketplace.authservice.core.sagas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import com.wks.servicemarketplace.authservice.api.AccountCreatedEvent
import com.wks.servicemarketplace.authservice.api.AuthMessaging
import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.OutboxDao
import com.wks.servicemarketplace.authservice.core.SagaDao
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.events.EventId
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import com.wks.servicemarketplace.common.sagas.DeadlineAfter
import com.wks.servicemarketplace.common.sagas.Saga
import com.wks.servicemarketplace.common.sagas.TransactionId
import com.wks.servicemarketplace.customerservice.api.CustomerCreatedEvent
import com.wks.servicemarketplace.customerservice.api.CustomerCreationFailedEvent
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.inject.Inject

enum class CreateProfileState : Saga.State {
    CREATING_PROFILE,
    PROFILE_CREATED,
    PROFILE_CREATION_FAILED;

    override fun stateName() = this.name
}

class CreateProfileSaga @Inject constructor(
    private val dataSource: DataSource,
    private val eventDao: EventDao,
    private val outboxDao: OutboxDao,
    private val sagaDao: SagaDao,
    private val objectMapper: ObjectMapper
) {

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

                eventDao.saveEvent(
                    conn, EventEnvelope(
                        eventId,
                        accountCreated.eventType,
                        payload,
                        accountCreated.uuid.toString(),
                        accountCreated.entityType
                    )
                )

                outboxDao.saveMessage(
                        conn,
                        Message.builder(messageId, accountCreated.eventType.name, payload, AuthMessaging.Exchange.MAIN.exchangeName)
                                .withDestinationRoutingKey(when (accountCreated.type) {
                                    UserType.SERVICE_PROVIDER -> AuthMessaging.RoutingKey.SERVICE_PROVIDER_ACCOUNT_CREATED
                                    UserType.CUSTOMER -> AuthMessaging.RoutingKey.CUSTOMER_ACCOUNT_CREATED
                                })
                                .withCorrelationId(transactionId.toString())
                                .build()
                )

                sagaDao.saveSaga(
                    conn, Saga(
                        transactionId,
                        SAGA_NAME,
                        accountCreated.uuid.toString(),
                        accountCreated.entityType,
                        CreateProfileState.CREATING_PROFILE,
                        DeadlineAfter(Duration.ofMinutes(1)),
                        eventId
                    )
                )

                conn.commit()

                return eventId
            } catch (e: Exception) {
                LOGGER.error("Failed to create profile ${e.message}", e)
                conn.rollback()
                throw e
            }
        }
    }

    fun on(
        correlationId: String,
        customerCreatedEvent: CustomerCreatedEvent
    ) {
        LOGGER.info("Received CustomerCreatedEvent: $customerCreatedEvent")
        val transactionId = TransactionId.fromString(correlationId)
        dataSource.connection().use { conn ->
            try {
                conn.autoCommit = false
                val saga = sagaDao.fetchSaga(conn, transactionId, CreateProfileState::valueOf)
                sagaDao.updateSaga(conn, transactionId, saga.copy(
                    state = CreateProfileState.PROFILE_CREATED,
                    deadline = null
                ), CreateProfileState.CREATING_PROFILE)
                conn.commit()
            } catch (e: Exception) {
                LOGGER.error("Failed to update saga state", e)
                conn.rollback()
            }
        }
    }

    fun on(
        correlationId: String,
        customerCreationFailedEvent: CustomerCreationFailedEvent
    ) {
        LOGGER.info("Received CustomerCreationFailedEvent: $customerCreationFailedEvent")
        val transactionId = TransactionId.fromString(correlationId)
        dataSource.connection().use { conn ->
            try {
                conn.autoCommit = false
                val saga = sagaDao.fetchSaga(conn, transactionId, CreateProfileState::valueOf)
                sagaDao.updateSaga(conn, transactionId, saga.copy(
                    state = CreateProfileState.PROFILE_CREATION_FAILED,
                    deadline = null
                ), CreateProfileState.CREATING_PROFILE)
                conn.commit()
            } catch (e: Exception) {
                LOGGER.error("Failed to update saga state", e)
                conn.rollback()
            }
        }
    }
}