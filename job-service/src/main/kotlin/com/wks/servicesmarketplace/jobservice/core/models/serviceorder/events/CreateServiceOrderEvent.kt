package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrderStatus
import java.time.ZonedDateTime

data class CreateServiceOrderEvent(val orderId: String,
                                   val customerId: Long,
                                   val serviceCategoryId: Long,
                                   val title: String,
                                   val description: String,
                                   val orderDateTime: ZonedDateTime,
                                   val status: ServiceOrderStatus,
                                   val createdBy: String,
                                   val version: Long)