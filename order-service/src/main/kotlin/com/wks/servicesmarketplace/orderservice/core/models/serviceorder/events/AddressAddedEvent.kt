package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.events

import java.math.BigDecimal

data class AddressAddedEvent(
        val externalId: Long,
        val customerExternalId: Long,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        val latitude: BigDecimal,
        val longitude: BigDecimal,
        val createdBy: String,
        val version: Long
)