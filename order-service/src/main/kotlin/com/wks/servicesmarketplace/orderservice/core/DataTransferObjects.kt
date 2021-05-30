package com.wks.servicesmarketplace.orderservice.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.Service
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceCode
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class OrderIdResponse(val orderId: OrderUUID)

data class ServiceOrderRequest constructor(
        @field:NotNull
        val address: Address,
        @field:ServiceCode
        val serviceCode: String,
        @field:NotBlank
        val title: String,
        @field:NotBlank
        val description: String,
        @field:NotNull
        val orderDateTime: OffsetDateTime
) {
    data class Address(
            @field:NotNull
            val line1: String,
            val line2: String? = null,
            @field:NotNull
            val city: String,
            @field:NotNull
            val country: String,
            @field:NotNull
            val latitude: BigDecimal,
            @field:NotNull
            val longitude: BigDecimal
    )
}

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