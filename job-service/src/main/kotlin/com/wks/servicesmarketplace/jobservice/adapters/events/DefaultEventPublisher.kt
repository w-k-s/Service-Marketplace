package com.wks.servicesmarketplace.jobservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicesmarketplace.jobservice.config.QueueName
import com.wks.servicesmarketplace.jobservice.core.events.EventPublisher
import com.wks.servicesmarketplace.jobservice.core.sagas.verifyserviceorder.VerifyAddressCommand
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class DefaultEventPublisher(private val rabbitAdmin: RabbitAdmin,
                            private val rabbitTemplate: RabbitTemplate,
                            private val objectMapper: ObjectMapper) : EventPublisher {

    override fun publish(verifyAddressCommand: VerifyAddressCommand) {
        val queueName = rabbitAdmin.declareQueue(Queue(QueueName.VERIFY_ADDRESS, true, false, true))

        val command = objectMapper.writeValueAsString(verifyAddressCommand)

        rabbitTemplate.convertAndSend(
                queueName!!,
                command
        )
    }
}