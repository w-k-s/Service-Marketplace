package com.wks.servicesmarketplace.orderservice.core.usecases

import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.repositories.ServiceOrderRepository
import org.springframework.stereotype.Service

data class FindServiceOrderByUUIDRequest(
        val orderUUID: OrderUUID,
        val authentication: Authentication
)

@Service
class GetServiceOrderByIdUseCase(private val serviceOrderRepository: ServiceOrderRepository) : UseCase<FindServiceOrderByUUIDRequest, ServiceOrderResponse> {

    override fun execute(request: FindServiceOrderByUUIDRequest): ServiceOrderResponse {
        return serviceOrderRepository.findById(request.orderUUID)
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
                .orElseThrow { CoreException(
                    ErrorType.RESOURCE_NOT_FOUND,
                    "Order ${request.orderUUID} not found"
                ) }
    }

}