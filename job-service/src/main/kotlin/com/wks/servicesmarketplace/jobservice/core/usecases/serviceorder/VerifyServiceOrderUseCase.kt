package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VerifyServiceOrderUseCase(val commandGateway: CommandGateway) : UseCase<String, Unit> {

    @Transactional
    override fun execute(orderId: String) {

        commandGateway.send<Void>(VerifyServiceOrderCommand(
                orderId,
                "Joe Doe" // TODO get from principal
        ))

    }
}