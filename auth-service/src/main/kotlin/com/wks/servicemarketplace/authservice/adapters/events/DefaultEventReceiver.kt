package com.wks.servicemarketplace.authservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import com.wks.servicemarketplace.common.auth.UserRole
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
        companyCreated(iamAdapter, objectMapper, channel)
    }

    private fun companyCreated(iamAdapter: IAMAdapter, objectMapper: ObjectMapper, channel: Channel) {
        channel.exchangeDeclare(Exchange.SERVICE_PROVIDER, BuiltinExchangeType.TOPIC, Durable.TRUE, AutoDelete.FALSE, Internal.FALSE, emptyMap())
        val queueName = channel.queueDeclare(
                Incoming.Queue.COMPANY_CREATED,
                Durable.TRUE,
                Exclusive.FALSE,
                AutoDelete.FALSE,
                emptyMap()
        ).queue
        channel.queueBind(queueName, Exchange.SERVICE_PROVIDER, Incoming.RoutingKey.COMPANY_CREATED)

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