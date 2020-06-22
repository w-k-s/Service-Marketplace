package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

class ServiceOrderRequest {
    @field:JsonProperty("customerId")
    var customerExternalId: Long = 0
    var addressExternalId: Long = 0
    var serviceCategoryId: Long = 0
    var title: String? = null
    var description: String? = null
    var orderDateTime: ZonedDateTime? = null
}
