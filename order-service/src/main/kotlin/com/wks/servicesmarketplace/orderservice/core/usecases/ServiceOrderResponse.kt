package com.wks.servicesmarketplace.orderservice.core.usecases

import com.wks.servicesmarketplace.orderservice.core.CustomerUUID
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderStatus
import java.time.OffsetDateTime

data class ServiceOrderResponse(
        val orderUUID: OrderUUID,
        val customerUUID: CustomerUUID,
        val serviceCode: String,
        val title: String,
        val description: String,
        val status: ServiceOrderStatus,
        val orderDateTime: OffsetDateTime,
        val createdDate: OffsetDateTime,
        val rejectReason: String? = null,
        val version: Long
)
