package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.events.VerifyServiceOrderEvent
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.orderservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class VerifyServiceOrderUseCase(private val commandGateway: CommandGateway,
                                private val serviceOrderQueryRepository: ServiceOrderQueryRepository) : UseCase<String, Unit> {

    override fun execute(orderId: String) {

        commandGateway.sendAndWait<Void>(VerifyServiceOrderCommand(
                orderId,
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