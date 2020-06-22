package com.wks.servicesmarketplace.jobservice.core.events

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities.Address
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.AddressAddedEvent
import com.wks.servicesmarketplace.jobservice.core.repositories.CustomerAddressQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AddressAddedEventHandler(private val customerAddressQueryRepository: CustomerAddressQueryRepository) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AddressAddedEventHandler::class.java)
    }

    fun onAddressAdded(event: AddressAddedEvent) {
        LOGGER.info("Received Address Added Event for customer id '{}', address id: '{}' name: '{}'",
                event.customerExternalId,
                event.externalId,
                event.name)

        val address = Address(
                event.externalId,
                event.customerExternalId,
                event.name,
                event.line1,
                event.line2,
                event.city,
                event.country,
                event.latitude,
                event.longitude,
                event.version
        )

        customerAddressQueryRepository.save(address)

        LOGGER.info("Saved Address '{}', id: '{}' for customer '{}'",
                address.name,
                address.externalId,
                address.customerExternalId)
    }
}