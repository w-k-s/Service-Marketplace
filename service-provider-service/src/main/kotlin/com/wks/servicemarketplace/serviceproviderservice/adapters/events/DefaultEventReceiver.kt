package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.wks.servicemarketplace.authservice.api.AuthMessaging
import com.wks.servicemarketplace.common.auth.TokenValidator
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRepresentativeRequest
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRepresentativeUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DefaultEventReceiver @Inject constructor(
    private val tokenValidator: TokenValidator,
    createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase,
    objectMapper: ObjectMapper,
    channel: Channel
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventReceiver::class.java)
        private const val QUEUE_SERVICE_PROVIDER_CREATED = "com.wks.servicemarketplace.serviceprovider.queue.serviceProviderCreated"
    }

    init {
        consumeServiceProviderCreated(createCompanyRepresentativeUseCase, objectMapper, channel)
    }

    private fun consumeServiceProviderCreated(
        createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase,
        objectMapper: ObjectMapper,
        channel: Channel
    ) {
        AuthMessaging.Exchange.MAIN.declare(channel)
        channel.queueDeclare(QUEUE_SERVICE_PROVIDER_CREATED, true, false, true, emptyMap())
        channel.queueBind(QUEUE_SERVICE_PROVIDER_CREATED, AuthMessaging.Exchange.MAIN.exchangeName, AuthMessaging.RoutingKey.SERVICE_PROVIDER_ACCOUNT_CREATED)
        LOGGER.info("Queue '${QUEUE_SERVICE_PROVIDER_CREATED}' listening to routing key ${AuthMessaging.RoutingKey.SERVICE_PROVIDER_ACCOUNT_CREATED}")

        channel.basicConsume(QUEUE_SERVICE_PROVIDER_CREATED, false, { consumerTag: String, message: Delivery ->
            try {
                LOGGER.info("Service Provider Created Event")

                val customerRequest =
                    objectMapper.readValue(message.body, CreateCompanyRepresentativeRequest.Builder::class.java).also {
                        it.authentication = tokenValidator.authenticate(message.authorization())
                        it.correlationId = message.properties.correlationId
                    }.build()
                createCompanyRepresentativeUseCase.execute(customerRequest)
                channel.basicAck(message.envelope.deliveryTag, false)
            } catch (e: CoreException) {
                LOGGER.error(e.message, e)
                // TODO: handle properly (e.g. put in error queue)
            }
        }, { _: String -> /*noop*/ })
    }


}