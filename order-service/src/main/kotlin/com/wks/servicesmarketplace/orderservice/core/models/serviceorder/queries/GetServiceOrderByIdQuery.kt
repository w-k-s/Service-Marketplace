package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.queries

import javax.validation.constraints.NotBlank

data class GetServiceOrderByIdQuery(
        @field:NotBlank val orderId: String?
)