package com.wks.servicesmarketplace.orderservice.core.usecases

import com.wks.servicesmarketplace.orderservice.core.*
import com.wks.servicesmarketplace.orderservice.core.exceptions.UserIdMissingException
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.security.RolesAllowed

@Service
@Transactional
class CreateServiceOrderUseCase(private val serviceOrderRepository: ServiceOrderRepository) : UseCase<ServiceOrderRequest, OrderIdResponse> {

    @RolesAllowed("ROLE_order.create")
    override fun execute(request: ServiceOrderRequest): OrderIdResponse {

        val orderId = OrderUUID.random()
        val customerId = CustomerUUID.fromString(request.authentication.user?.id ?: throw UserIdMissingException())
        serviceOrderRepository.save(request.let {
            ServiceOrder.create(
                    orderId,
                    customerId,
                    it.serviceCode,
                    it.title,
                    it.description,
                    Address.create(
                            it.address.line1,
                            it.address.line2,
                            it.address.city,
                            CountryCode.of(it.address.country),
                            it.address.latitude,
                            it.address.longitude
                    ),
                    it.orderDateTime,
                    ServiceOrderStatus.VERIFYING,
                    request.authentication
            )
        })

        return OrderIdResponse(orderId)
    }
}