package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.wks.servicemarketplace.authservice.core.sagas.CreateProfileSaga
import com.wks.servicemarketplace.authservice.messaging.AuthMessaging
import com.wks.servicemarketplace.common.auth.TokenValidator
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreatedEvent
import com.wks.servicemarketplace.customerservice.messaging.CustomerCreationFailedEvent
import com.wks.servicemarketplace.customerservice.messaging.CustomerMessaging
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DefaultEventReceiver @Inject constructor(
    private val createProfileSaga: CreateProfileSaga,
    private val tokenValidator: TokenValidator,
    private val objectMapper: ObjectMapper,
    private val channel: Channel
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventReceiver::class.java)
    }

    init {
        customerCreated()
        customerCreationFailed()
    }

    private fun customerCreated() {
        CustomerMessaging.Exchange.MAIN.declare(channel)
        AuthMessaging.Queue.CUSTOMER_PROFILE_CREATED.declare(channel);
        channel.queueBind(
            AuthMessaging.Queue.CUSTOMER_PROFILE_CREATED.queueName,
            CustomerMessaging.Exchange.MAIN.exchangeName,
            CustomerMessaging.RoutingKey.CUSTOMER_PROFILE_CREATED
        )
        channel.basicConsume(
            AuthMessaging.Queue.CUSTOMER_PROFILE_CREATED.queueName,
            false,
            { _: String, message: Delivery ->
                try {
                    message.properties.correlationId?.let{ correlationId ->
                        createProfileSaga.on(
                            correlationId,
                            objectMapper.readValue(message.body, CustomerCreatedEvent::class.java)
                        )
                    }
                    channel.basicAck(message.envelope.deliveryTag, false)
                } catch (e: Exception) {
                    LOGGER.error(e.message, e)
                }
            },
            { _: String -> /*noop*/ })
    }

    private fun customerCreationFailed() {
        CustomerMessaging.Exchange.MAIN.declare(channel)
        AuthMessaging.Queue.CUSTOMER_PROFILE_CREATION_FAILED.declare(channel);
        channel.queueBind(
            AuthMessaging.Queue.CUSTOMER_PROFILE_CREATION_FAILED.queueName,
            CustomerMessaging.Exchange.MAIN.exchangeName,
            CustomerMessaging.RoutingKey.CUSTOMER_PROFILE_CREATION_FAILED
        )
        channel.basicConsume(
            AuthMessaging.Queue.CUSTOMER_PROFILE_CREATION_FAILED.queueName,
            false,
            { _: String, message: Delivery ->
                try {
                    message.properties.correlationId?.let{ correlationId ->
                        createProfileSaga.on(
                            correlationId,
                            objectMapper.readValue(message.body, CustomerCreationFailedEvent::class.java)
                        )
                    }
                    channel.basicAck(message.envelope.deliveryTag, false)
                } catch (e: Exception) {
                    LOGGER.error(e.message, e)
                }
            },
            { _: String -> /*noop*/ })
    }
}

fun Delivery.authorizationToken(): String {
    return properties.headers["Authorization"].toString().substring("Bearer".length).trim()
}