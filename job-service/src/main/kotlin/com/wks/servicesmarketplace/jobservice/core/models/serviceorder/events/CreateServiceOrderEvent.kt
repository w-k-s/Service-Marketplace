package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrderStatus
import java.math.BigDecimal
import java.time.ZonedDateTime

data class CreateServiceOrderEvent(val orderId: String,
                                   val customerId: Long,
                                   val serviceCategoryId: Long,
                                   val title: String,
                                   val description: String,
                                   val address: Address,
                                   val orderDateTime: ZonedDateTime,
                                   val status: ServiceOrderStatus,
                                   val createdBy: String){
    data class Address(
            val externalId: Long,
            val name: String,
            val line1: String,
            val line2: String?,
            val city: String,
            val country: String,
            val latitude: BigDecimal,
            val longitude: BigDecimal
    )
}