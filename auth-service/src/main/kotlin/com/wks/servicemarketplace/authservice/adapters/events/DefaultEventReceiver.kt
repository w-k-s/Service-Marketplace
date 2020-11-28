package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import com.wks.servicemarketplace.authservice.core.UserRole
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class CompanyCreatedEvent constructor(val createdBy: String)

class DefaultEventReceiver @Inject constructor(iamAdapter: IAMAdapter,
                                               objectMapper: ObjectMapper,
                                               channel: Channel) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultEventReceiver::class.java)
    }

    init {
        channel.exchangeDeclare(Exchange.ACCOUNT, BuiltinExchangeType.TOPIC, true, true, emptyMap())
        companyCreated(iamAdapter, objectMapper, channel)
    }

    private fun companyCreated(iamAdapter: IAMAdapter, objectMapper: ObjectMapper, channel: Channel) {
        val queueName = channel.queueDeclare(
                Queue.COMPANY_CREATED,
                true,
                true,
                true,
                emptyMap()
        ).queue
        channel.queueBind(queueName, Exchange.ACCOUNT, RoutingKey.SERVICE_PROVIDER_CREATED)

        channel.basicConsume(queueName, false, { _: String, message: Delivery ->
            try {
                val userId = objectMapper.readValue(message.body, CompanyCreatedEvent::class.java).createdBy
                iamAdapter.assignRole(UserRole.SERVICE_PROVIDER, userId)
                channel.basicAck(message.envelope.deliveryTag, false)
            } catch (e: Exception) {
                LOGGER.error(e.message, e)
                // TODO: handle properly (e.g. put in error queue)
            }
        }, { _: String -> /*noop*/ })
    }
}