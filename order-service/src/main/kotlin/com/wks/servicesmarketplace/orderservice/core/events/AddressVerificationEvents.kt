package com.wks.servicesmarketplace.orderservice.core.events

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.AddressId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddressVerifiedEvent(@TargetAggregateIdentifier val orderId: OrderUUID,
                                val customerExternalId: CustomerId,
                                val addressExternalId: AddressId)

data class AddressVerificationFailedEvent(
        @TargetAggregateIdentifier
        val orderId: OrderUUID,
        val code: Long,
        val type: String,
        val description: String?,
        val userInfo: Map<String,String>
)