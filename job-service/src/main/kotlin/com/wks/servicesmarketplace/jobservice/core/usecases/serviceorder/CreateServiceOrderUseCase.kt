package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrder
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.Repository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateServiceOrderUseCase(val commandGateway: CommandGateway,
                                val serviceOrderRepository: Repository<ServiceOrder>) : UseCase<ServiceOrderRequest, ServiceOrderResponse> {

    override fun execute(request: ServiceOrderRequest): ServiceOrderResponse {
        // TODO: get address from CQRS query side

        val orderId = UUID.randomUUID().toString()
        commandGateway.send<Void>(CreateServiceOrderCommand(
                orderId,
                request.customerId,
                request.serviceCategoryId,
                request.title,
                request.description,
                request.orderDateTime,
                "Joe Doe" // TODO get from principal
        ))

        // TODO: start saga

        return ServiceOrderResponse(orderId)
    }
}