package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCaseRequest
import java.time.ZonedDateTime

class ServiceOrderRequest : UseCaseRequest {
    @field:JsonProperty("customerId")
    var customerId: Long = 0
    var serviceCategoryId: Long = 0
    var title: String? = null
    var description: String? = null
    var orderDateTime: ZonedDateTime? = null
}
