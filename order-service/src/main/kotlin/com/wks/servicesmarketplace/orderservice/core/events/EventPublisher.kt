package com.wks.servicesmarketplace.orderservice.core.events

import com.wks.servicesmarketplace.orderservice.core.sagas.verifyserviceorder.VerifyAddressCommand
import org.springframework.stereotype.Component

@Component
interface EventPublisher {
    fun publish(verifyAddressCommand: VerifyAddressCommand)
}