package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates

import org.axonframework.modelling.command.EntityId
import java.math.BigDecimal

data class Address(
        @field:EntityId
        val externalId: Long,
        val customerExternalId: Long,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        var latitude: BigDecimal,
        var longitude: BigDecimal
)