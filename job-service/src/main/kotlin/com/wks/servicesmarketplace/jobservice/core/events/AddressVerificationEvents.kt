package com.wks.servicesmarketplace.jobservice.core.events

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddressVerifiedEvent(@TargetAggregateIdentifier val orderId: String,
                                val customerExternalId: String,
                                val addressExternalId: String)

data class AddressVerificationFailedEvent(
        @TargetAggregateIdentifier
        val orderId: String,
        val code: Long,
        val type: String,
        val description: String?,
        val userInfo: Map<String,String>
)