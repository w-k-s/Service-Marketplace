package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.queries

import javax.validation.constraints.NotBlank

data class GetServiceOrderByIdQuery(
        @field:NotBlank val orderId: String?
)