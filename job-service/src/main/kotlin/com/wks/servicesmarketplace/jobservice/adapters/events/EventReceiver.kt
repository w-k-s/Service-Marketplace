package com.wks.servicesmarketplace.jobservice.adapters.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicesmarketplace.jobservice.config.QueueName.Companion.CUSTOMER_ADDRESS_ADDED
import com.wks.servicesmarketplace.jobservice.core.events.AddressAddedEventHandler
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.AddressAddedEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class EventReceiver(private val objectMapper: ObjectMapper,
                    private val addressAddedEventHandler: AddressAddedEventHandler) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventReceiver::class.java)
    }

    @RabbitListener(queues = [CUSTOMER_ADDRESS_ADDED])
    @Throws(InterruptedException::class)
    fun receive1(message: String?) {
        message?.let {
            LOGGER.info("Received Customer Address Added Event")

            val addressAddedEvent = objectMapper.readValue(message, AddressAddedEvent::class.java)

            LOGGER.info("Customer Address Added Event where customer id: '{}', address id: '{}'",
                    addressAddedEvent.customerExternalId,
                    addressAddedEvent.externalId)

            addressAddedEventHandler.onAddressAdded(addressAddedEvent)
        }
    }
}