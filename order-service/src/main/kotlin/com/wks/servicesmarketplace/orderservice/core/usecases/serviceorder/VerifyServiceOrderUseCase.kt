package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.aggregates.VerifyServiceOrderEvent
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.orderservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class VerifyServiceOrderUseCase(private val commandGateway: CommandGateway,
                                private val serviceOrderQueryRepository: ServiceOrderQueryRepository) : UseCase<OrderUUID, Unit> {

    override fun execute(request: OrderUUID) {

        commandGateway.sendAndWait<Void>(VerifyServiceOrderCommand(
                request,
                "Joe Doe" // TODO get from principal
        ), 1, TimeUnit.SECONDS)
    }

    @EventHandler
    @Transactional
    fun verify(event: VerifyServiceOrderEvent) {
        serviceOrderQueryRepository.findById(event.orderId).map {
            it.verify(
                    event.modifiedBy
            )
        }.ifPresent {
            serviceOrderQueryRepository.save(it)
        }
    }
}