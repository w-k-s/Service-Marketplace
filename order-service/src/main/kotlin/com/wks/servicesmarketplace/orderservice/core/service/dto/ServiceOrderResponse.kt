package com.wks.servicesmarketplace.orderservice.core.service.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.Service
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderStatus
import java.time.OffsetDateTime

data class ServiceOrderResponse(
        val orderUUID: OrderUUID,
        val customerUUID: CustomerUUID,
        @JsonIgnore
        val service: Service,
        val title: String,
        val description: String,
        val status: ServiceOrderStatus,
        val orderDateTime: OffsetDateTime,
        val createdDate: OffsetDateTime,
        val rejectReason: String? = null,
        val version: Long
){
    val serviceCode = service.code
}
