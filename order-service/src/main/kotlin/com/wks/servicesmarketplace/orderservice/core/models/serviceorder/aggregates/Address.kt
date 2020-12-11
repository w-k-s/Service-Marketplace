package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.aggregates

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.AddressId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import org.axonframework.modelling.command.EntityId
import java.math.BigDecimal

data class Address(
        @field:EntityId
        val externalId: AddressId,
        val customerExternalId: CustomerId,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        var latitude: BigDecimal,
        var longitude: BigDecimal
)