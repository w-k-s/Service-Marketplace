package com.wks.servicesmarketplace.jobservice.core.events

import com.wks.servicesmarketplace.jobservice.core.sagas.verifyserviceorder.VerifyAddressCommand
import org.springframework.stereotype.Component

@Component
interface EventPublisher {
    fun publish(verifyAddressCommand: VerifyAddressCommand)
}