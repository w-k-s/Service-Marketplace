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
                                               private val createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase,
                                               objectMapper: ObjectMapper,
                                               channel: Channel) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventReceiver::class.java)
    }

    init {
        channel.exchangeDeclare(Exchange.ACCOUNT, BuiltinExchangeType.TOPIC, true, true, emptyMap())
        consumeServiceProviderCreated(createCompanyRepresentativeUseCase, objectMapper, channel)
    }

    private fun consumeServiceProviderCreated(createCompanyRepresentativeUseCase: CreateCompanyRepresentativeUseCase, objectMapper: ObjectMapper, channel: Channel) {
        val queueName = channel.queueDeclare(
                Queue.SERVICE_PROVIDER_CREATED,
                true,
                true,
                true,
                emptyMap()
        ).queue
        channel.queueBind(queueName, Exchange.ACCOUNT, RoutingKey.SERVICE_PROVIDER_CREATED)

        channel.basicConsume(queueName, false, { consumerTag: String, message: Delivery ->
            try {
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