package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands

import java.math.BigDecimal
import javax.validation.constraints.*

class CreateServiceOrderAddressCommand(
        @field:NotNull val externalId: Long?,
        @field:NotBlank val name: String?,
        @field:NotNull @field:NotBlank val line1: String?,
        val line2: String?,
        @field:NotNull @field:NotBlank val city: String,
        @field:NotNull @field:Size(min = 2, max = 2) val country: String,
        @field:DecimalMin("-90") @field:DecimalMax("90") val latitude: BigDecimal,
        @field:DecimalMin("-180") @field:DecimalMax("180") val longitude: BigDecimal
)