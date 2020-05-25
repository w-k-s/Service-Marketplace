package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

import java.time.ZonedDateTime

data class CreateServiceOrderEvent(val orderId: String,
                                   val customerId: Long,
                                   val serviceCategoryId: Long,
                                   val title: String,
                                   val description: String,
                                   val orderDateTime: ZonedDateTime,
                                   val createdBy: String){
}