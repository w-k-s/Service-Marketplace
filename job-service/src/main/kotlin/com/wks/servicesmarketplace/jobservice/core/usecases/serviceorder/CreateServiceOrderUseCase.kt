package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.exceptions.AddressNotFoundException
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities.ServiceOrder
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.CreateServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.repositories.CustomerAddressQueryRepository
import com.wks.servicesmarketplace.jobservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.jobservice.core.usecases.UseCase
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateServiceOrderUseCase(private val commandGateway: CommandGateway,
                                private val serviceOrderQueryRepository: ServiceOrderQueryRepository,
                                private val customerAddressQueryRepository: CustomerAddressQueryRepository) : UseCase<ServiceOrderRequest, OrderIdResponse> {

    override fun execute(request: ServiceOrderRequest): OrderIdResponse {

        val address = customerAddressQueryRepository.findByExternalIdAndCustomerExternalId(
                request.addressExternalId,
                request.customerExternalId
        ) ?: throw AddressNotFoundException(request.addressExternalId, request.customerExternalId)

        val orderId = UUID.randomUUID().toString()
        commandGateway.send(CreateServiceOrderCommand(
                orderId,
                request.customerExternalId,
                request.serviceCategoryId,
                request.title,
                request.description,
                CreateServiceOrderCommand.Address(
                        address.externalId,
                        address.name,
                        address.line1,
                        address.line2,
                        address.city,
                        address.country,
                        address.latitude,
                        address.longitude
                ),
                request.orderDateTime,
                "Joe Doe" // TODO get from principal
        ), LoggingCallback.INSTANCE)

        // TODO: start saga

        return OrderIdResponse(orderId)
    }

    @EventHandler
    fun saveServiceOrder(event: CreateServiceOrderEvent) {
        serviceOrderQueryRepository.save(event.let {
            ServiceOrder.create(
                    it.orderId,
                    it.customerId,
                    it.serviceCategoryId,
                    it.title,
                    it.description,
                    it.address.externalId,
                    it.orderDateTime,
                    it.status,
                    it.createdBy
            )
        })
    }
}