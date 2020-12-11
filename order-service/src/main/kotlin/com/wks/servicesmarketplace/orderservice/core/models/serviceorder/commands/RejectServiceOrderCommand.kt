package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RejectServiceOrderCommand(
        @field:TargetAggregateIdentifier val orderId: OrderUUID?,
        @field:Size(min = 15, max = 150) @field:NotBlank val rejectReason: String?,
        @field:NotBlank var modifiedBy: String?
)