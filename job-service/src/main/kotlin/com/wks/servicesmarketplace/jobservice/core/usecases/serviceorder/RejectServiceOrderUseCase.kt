package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.RejectServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.RejectServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RejectServiceOrderUseCase(private val commandGateway: CommandGateway,
                                private val serviceOrderQueryRepository: ServiceOrderQueryRepository) : UseCase<RejectServiceOrderRequest, Unit> {

    override fun execute(request: RejectServiceOrderRequest) {
        commandGateway.sendAndWait<Void>(RejectServiceOrderCommand(
                request.orderId,
                request.rejectReason,
                "John Doe" // TODO get from Principal
        ), 1, TimeUnit.SECONDS)
    }

    @EventHandler
    fun reject(event: RejectServiceOrderEvent) {
        serviceOrderQueryRepository.findById(event.orderId).map {
            it.reject(event.rejectReason, event.modifiedBy)
        }.ifPresent {
            serviceOrderQueryRepository.save(it)
        }
    }
}