package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.events.CreateServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

@Service
class CreateServiceOrderUseCase(val commandGateway: CommandGateway) : UseCase<ServiceOrderRequest, ServiceOrderResponse> {

    @Transactional
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

        // start saga

        return ServiceOrderResponse(orderId,
                request.customerId,
                request.serviceCategoryId,
                request.title!!,
                request.description!!,
                request.orderDateTime!!)
    }
}