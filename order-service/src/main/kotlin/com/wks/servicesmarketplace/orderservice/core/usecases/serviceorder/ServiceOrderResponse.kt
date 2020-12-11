package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.fasterxml.jackson.annotation.JsonInclude
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.ServiceOrderStatus
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServiceOrderResponse(
        val orderId: OrderUUID,
        val customerId: CustomerId,
        val serviceCode: String,
        val title: String,
        val description: String,
        val status: ServiceOrderStatus,
        val orderDateTime: OffsetDateTime,
        val createdDate: OffsetDateTime,
        val rejectReason: String? = null,
        val version: Long
)
