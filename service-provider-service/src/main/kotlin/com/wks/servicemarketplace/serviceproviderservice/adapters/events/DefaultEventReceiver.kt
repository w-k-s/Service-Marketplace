package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.InvalidTokenException
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.TokenValidator
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CoreRuntimeException
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRepresentativeRequest
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRepresentativeUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DefaultEventReceiver @Inject constructor(private val tokenValidator: TokenValidator,
                                               createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase,
                                               objectMapper: ObjectMapper,
                                               channel: Channel) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventReceiver::class.java)
    }

    init {
        consumeServiceProviderCreated(createCompanyRepresentativeUseCase, objectMapper, channel)
    }

    private fun consumeServiceProviderCreated(createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase, objectMapper: ObjectMapper, channel: Channel) {
        LOGGER.info("Declaring exchange: ${Exchange.ACCOUNT}")
        channel.exchangeDeclare(Exchange.ACCOUNT, BuiltinExchangeType.TOPIC, true, true,false, emptyMap())

        val queueName = channel.queueDeclare(
                Incoming.Queue.SERVICE_PROVIDER_CREATED,
                true,
                false,
                true,
                emptyMap()
        ).queue
        channel.queueBind(queueName, Exchange.ACCOUNT, Incoming.RoutingKey.SERVICE_PROVIDER_CREATED)
        LOGGER.info("Queue '$queueName' listening to routing key ${Incoming.RoutingKey.SERVICE_PROVIDER_CREATED}")

        channel.basicConsume(queueName, false, { consumerTag: String, message: Delivery ->
            try {
                LOGGER.info("Service Provider Created Event")

                val customerRequest = objectMapper.readValue(message.body, CreateCompanyRepresentativeRequest.Builder::class.java)
                        .authentication(tokenValidator.authenticate(message.authorization()))
                        .build()
                createCompanyRepresentativeUseCase.execute(customerRequest)
                channel.basicAck(message.envelope.deliveryTag, false)
            } catch (e: CoreRuntimeException) {
                LOGGER.error(e.message, e)
                // TODO: handle properly (e.g. put in error queue)
            } catch (e: InvalidTokenException) {
                LOGGER.error(e.message, e)
            }
        }, { _: String -> /*noop*/ })
    }


}