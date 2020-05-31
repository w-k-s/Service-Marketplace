package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RejectServiceOrderCommand(
        @field:TargetAggregateIdentifier val orderId: String?,
        @field:Size(min = 15, max = 150) @field:NotBlank val rejectReason: String?,
        @field:NotBlank var modifiedBy: String?
)