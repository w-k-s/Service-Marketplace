package com.wks.servicesmarketplace.orderservice.adapters.web

import com.wks.servicesmarketplace.orderservice.core.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.queries.GetServiceOrderByIdQuery
import com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/orders")
class ServiceOrdersController(val createServiceOrderUseCase: CreateServiceOrderUseCase,
                              val verifyServiceOrderUseCase: VerifyServiceOrderUseCase,
                              val rejectServiceOrderUseCase: RejectServiceOrderUseCase,
                              val getServiceOrderByIdUseCase: GetServiceOrderByIdUseCase) {

    @PostMapping("/")
    fun create(@AuthenticationPrincipal principal: Principal, @RequestBody(required = true) serviceOrder: ServiceOrderRequest.Builder) =
            createServiceOrderUseCase.execute(
                    serviceOrder.authentication(principal as Authentication).build()
            )

    @PostMapping("/{orderId}/verify")
    fun verify(@PathVariable("orderId", required = true) orderId: String) = verifyServiceOrderUseCase.execute(orderId)

    @PostMapping("/{orderId}/reject")
    fun reject(@PathVariable("orderId", required = true) orderId: String, @RequestBody(required = true) rejectRequest: RejectServiceOrderRequest) = rejectServiceOrderUseCase.execute(RejectServiceOrderRequest(orderId, rejectRequest.rejectReason))

    @GetMapping("/{orderId}")
    fun getServiceOrderById(@PathVariable("orderId", required = true) orderId: String): ServiceOrderResponse = getServiceOrderByIdUseCase.execute(GetServiceOrderByIdQuery(orderId))
}