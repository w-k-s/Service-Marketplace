package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

data class VerifyServiceOrderEvent(
        val orderId: String,
        val modifiedBy: String
)