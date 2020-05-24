package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.usecases.UseCaseResponse
import java.time.ZonedDateTime

data class ServiceOrderResponse(val orderId: String,
                                val customerId: Long,
                               val serviceCategoryId: Long,
                               val title: String,
                               val description: String,
                               val orderDateTime: ZonedDateTime) : UseCaseResponse