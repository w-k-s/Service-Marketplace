package com.wks.servicemarketplace.messaging

import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.events.EventType
import com.wks.servicemarketplace.common.events.FailureEvent

data class ServiceProviderCreationFailedEvent constructor(
    override val errorType: ErrorType,
    override val description: String?
) : FailureEvent {
    override val entityType = "ServiceProvider"
    override val eventType = EventType.SERVICE_PROVIDER_PROFILE_CREATION_FAILED
}