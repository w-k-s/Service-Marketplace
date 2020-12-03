package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.events

data class VerifyServiceOrderEvent(
        val orderId: String,
        val modifiedBy: String
)