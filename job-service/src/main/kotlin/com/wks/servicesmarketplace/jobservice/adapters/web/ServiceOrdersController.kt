package com.wks.servicesmarketplace.jobservice.adapters.web

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.queries.GetServiceOrderByIdQuery
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders/v1/")
class ServiceOrdersController(val createServiceOrderUseCase: CreateServiceOrderUseCase,
                              val verifyServiceOrderUseCase: VerifyServiceOrderUseCase,
                              val rejectServiceOrderUseCase: RejectServiceOrderUseCase,
                              val getServiceOrderByIdUseCase: GetServiceOrderByIdUseCase) {

    @PostMapping("/")
    fun create(@RequestBody(required = true) serviceOrder: ServiceOrderRequest) = createServiceOrderUseCase.execute(serviceOrder)

    @PostMapping("/{orderId}/verify")
    fun verify(@PathVariable("orderId", required = true) orderId: String) = verifyServiceOrderUseCase.execute(orderId)

    @PostMapping("/{orderId}/reject")
    fun reject(@PathVariable("orderId", required = true) orderId: String, @RequestBody(required = true) rejectRequest: RejectServiceOrderRequest)
            = rejectServiceOrderUseCase.execute(RejectServiceOrderRequest(orderId, rejectRequest.rejectReason))

    @GetMapping("/{orderId}")
    fun getServiceOrderById(@PathVariable("orderId", required = true) orderId: String): ServiceOrderResponse
            = getServiceOrderByIdUseCase.execute(GetServiceOrderByIdQuery(orderId))
}