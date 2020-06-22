package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

import java.math.BigDecimal

data class CreateServiceOrderAddressEvent(
        val externalId: Long,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        val latitude: BigDecimal,
        val longitude: BigDecimal
)