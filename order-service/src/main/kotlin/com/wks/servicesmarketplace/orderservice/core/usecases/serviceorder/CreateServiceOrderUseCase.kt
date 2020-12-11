package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.exceptions.CoreException
import com.wks.servicesmarketplace.orderservice.core.exceptions.ErrorType
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.aggregates.CreateServiceOrderEvent
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.ServiceOrder
import com.wks.servicesmarketplace.orderservice.core.repositories.CustomerAddressQueryRepository
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderQueryRepository
import com.wks.servicesmarketplace.orderservice.core.usecases.UseCase
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import javax.annotation.security.RolesAllowed

@Service
class CreateServiceOrderUseCase(private val commandGateway: CommandGateway,
                                private val serviceOrderQueryRepository: ServiceOrderQueryRepository,
                                private val customerAddressQueryRepository: CustomerAddressQueryRepository) : UseCase<ServiceOrderRequest, OrderIdResponse> {

    @RolesAllowed("ROLE_order.create")
    override fun execute(request: ServiceOrderRequest): OrderIdResponse {

        val address = customerAddressQueryRepository.findByExternalIdAndCustomerExternalId(
                request.addressExternalId,
                request.customerExternalId
        ) ?: throw CoreException(ErrorType.ADDRESS_NOT_FOUND, "Address not found")

        val orderId = OrderUUID.random()
        commandGateway.send(CreateServiceOrderCommand(
                orderId,
                request.customerExternalId,
                request.serviceCode,
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
                        address.longitude,
                        address.version
                ),
                request.orderDateTime,
                request.authentication.user!!.id
        ), LoggingCallback.INSTANCE)

        return OrderIdResponse(orderId)
    }

    @EventHandler
    fun saveServiceOrder(event: CreateServiceOrderEvent) {
        serviceOrderQueryRepository.save(event.let {
            ServiceOrder.create(
                    it.orderId,
                    it.customerId,
                    it.serviceCode,
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