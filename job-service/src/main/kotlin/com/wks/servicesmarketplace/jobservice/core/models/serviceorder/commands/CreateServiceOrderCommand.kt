package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.constraints.datetime.ServiceOrderDateTime
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.hibernate.validator.constraints.Length
import java.time.ZonedDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class CreateServiceOrderCommand(@field:TargetAggregateIdentifier @field:NotBlank var orderId:  String?,
                                @field:Min(value = 1)  var customerId: Long = 0,
                                @field:Min(value = 1) var serviceCategoryId:  Long = 0,
                                @field:NotEmpty @field:Length(min = 15, max = 150)var title: String?,
                                @field:NotBlank @field:Length(min = 30, max = 255) var description: String?,
                                @field:NotNull @field:ServiceOrderDateTime var orderDateTime: ZonedDateTime?,
                                @field:NotBlank var createdBy: String?)
