package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.constraints.datetime.ServiceOrderDateTime
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.validation.constraints.*

class CreateServiceOrderCommand(@field:TargetAggregateIdentifier @field:NotBlank var orderId: String?,
                                @field:Min(value = 1) var customerId: Long = 0,
                                @field:Min(value = 1) var serviceCategoryId: Long = 0,
                                @field:NotEmpty @field:Length(min = 15, max = 150) var title: String?,
                                @field:NotBlank @field:Length(min = 30, max = 255) var description: String?,
                                @field:NotNull var address: Address?,
                                @field:NotNull @field:ServiceOrderDateTime var orderDateTime: ZonedDateTime?,
                                @field:NotBlank var createdBy: String?){
    class Address(
            @field:NotNull val externalId: Long?,
            @field:NotBlank val name: String?,
            @field:NotNull @field:NotBlank val line1: String?,
            val line2: String?,
            @field:NotNull @field:NotBlank val city: String,
            @field:NotNull @field:Size(min = 2, max = 2) val country: String,
            @field:DecimalMin("-90") @field:DecimalMax("90") val latitude: BigDecimal,
            @field:DecimalMin("-180") @field:DecimalMax("180") val longitude: BigDecimal
    )
}