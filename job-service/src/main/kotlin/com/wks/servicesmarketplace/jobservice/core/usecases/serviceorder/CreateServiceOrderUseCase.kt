package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateServiceOrderUseCase(val commandGateway: CommandGateway) : UseCase<ServiceOrderRequest, ServiceOrderResponse> {

    @Transactional(propagation = Propagation.REQUIRED)
    override fun execute(request: ServiceOrderRequest): ServiceOrderResponse {
        // TODO: get address from CQRS query side

        val orderId = UUID.randomUUID().toString()
        commandGateway.send(CreateServiceOrderCommand(
                orderId,
                request.customerId,
                request.serviceCategoryId,
                request.title,
                request.description,
                request.orderDateTime,
                "Joe Doe" // TODO get from principal
        ), LoggingCallback.INSTANCE)

        // TODO: start saga

        return ServiceOrderResponse(orderId)
    }
}