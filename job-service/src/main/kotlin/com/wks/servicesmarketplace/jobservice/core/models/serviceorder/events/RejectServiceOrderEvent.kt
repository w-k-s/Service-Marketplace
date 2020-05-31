package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

data class RejectServiceOrderEvent(
        val orderId: String,
        val rejectReason: String,
        val modifiedBy: String
)