package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.constraints.NotBlank

data class VerifyServiceOrderCommand(
        @field:TargetAggregateIdentifier val orderId: String?,
        @field:NotBlank var modifiedBy: String?
)