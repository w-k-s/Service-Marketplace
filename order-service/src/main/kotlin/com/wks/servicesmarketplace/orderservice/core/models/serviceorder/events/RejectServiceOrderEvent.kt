package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.events

data class RejectServiceOrderEvent(
        val orderId: String,
        val rejectReason: String,
        val modifiedBy: String
)