package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.fasterxml.jackson.annotation.JsonInclude
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.ServiceOrderStatus
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServiceOrderResponse(
        val orderId: String,
        val customerId: Long,
        val serviceCategoryId: Long,
        val title: String,
        val description: String,
        val status: ServiceOrderStatus,
        val orderDateTime: ZonedDateTime,
        val createdDate: ZonedDateTime,
        val rejectReason: String? = null,
        val version: Long
)
