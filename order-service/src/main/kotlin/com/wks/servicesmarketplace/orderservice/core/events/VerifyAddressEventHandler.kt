package com.wks.servicesmarketplace.orderservice.core.events

import org.axonframework.eventhandling.gateway.EventGateway
import org.springframework.stereotype.Component

@Component
class VerifyAddressEventHandler(private val eventGateway: EventGateway) {


    fun onAddressVerified(event: AddressVerifiedEvent) {
        eventGateway.publish(event)
    }

    fun onAddressVerificationFailed(event: AddressVerificationFailedEvent) {
        eventGateway.publish(event)
    }
}