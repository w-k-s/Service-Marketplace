package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.VerifyServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
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