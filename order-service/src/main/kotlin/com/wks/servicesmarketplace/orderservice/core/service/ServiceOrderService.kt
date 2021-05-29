package com.wks.servicesmarketplace.orderservice.core.service

import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicesmarketplace.orderservice.core.Address
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.ServiceOrder
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderStatus
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderRepository
import com.wks.servicesmarketplace.orderservice.core.service.dto.OrderIdResponse
import com.wks.servicesmarketplace.orderservice.core.service.dto.ServiceOrderRequest
import com.wks.servicesmarketplace.orderservice.core.service.dto.ServiceOrderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.security.RolesAllowed

@Service
@Transactional
class ServiceOrderService constructor(val serviceOrderRepository: ServiceOrderRepository) {

    @RolesAllowed("ROLE_order.create")
    fun create(request: ServiceOrderRequest, authentication: Authentication): OrderIdResponse {

        val orderId = OrderUUID.random()
        val customerId = CustomerUUID.of(authentication.userId
                ?: throw CoreException(ErrorType.AUTHENTICATION, "userId not found"))

        serviceOrderRepository.save(request.let {
            ServiceOrder.create(
                    orderId,
                    customerId,
                    com.wks.servicemarketplace.common.Service.of(it.serviceCode),
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
                    authentication
            )
        })

        return OrderIdResponse(orderId)
    }

    fun get(orderUUID: OrderUUID, authentication: Authentication): ServiceOrderResponse {
        return serviceOrderRepository.findById(orderUUID)
                .map {
                    ServiceOrderResponse(
                            it.orderUUID,
                            it.customerUUID,
                            it.serviceCode,
                            it.title,
                            it.description,
                            it.status,
                            it.orderDateTime,
                            it.createdDate,
                            it.rejectReason,
                            it.version
                    )
                }
                .orElseThrow {
                    CoreException(
                            ErrorType.RESOURCE_NOT_FOUND,
                            "Order $orderUUID not found"
                    )
                }
    }
}