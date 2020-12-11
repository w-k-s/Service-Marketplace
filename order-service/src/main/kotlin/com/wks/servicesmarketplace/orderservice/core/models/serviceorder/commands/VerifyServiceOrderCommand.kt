package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.constraints.NotBlank

data class VerifyServiceOrderCommand(
        @field:TargetAggregateIdentifier val orderId: OrderUUID?,
        @field:NotBlank var modifiedBy: String?
)