package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

class ServiceOrderRequest {
    var customerExternalId: Long = 0
    var addressExternalId: Long = 0
    var serviceCategoryId: Long = 0
    var title: String? = null
    var description: String? = null
    var orderDateTime: String? = null
}
