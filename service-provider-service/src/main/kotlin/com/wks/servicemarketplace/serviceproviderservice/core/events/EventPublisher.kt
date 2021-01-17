package com.wks.servicemarketplace.serviceproviderservice.core.events

import com.wks.servicemarketplace.messaging.CompanyCreatedEvent
import java.io.IOException

interface EventPublisher {
    @Throws(IOException::class)
    fun companyCreated(token: String, event: CompanyCreatedEvent)
}