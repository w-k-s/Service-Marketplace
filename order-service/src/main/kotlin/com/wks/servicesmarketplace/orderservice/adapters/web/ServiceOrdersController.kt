package com.wks.servicesmarketplace.orderservice.adapters.web

import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.usecases.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/orders")
class ServiceOrdersController(val createServiceOrderUseCase: CreateServiceOrderUseCase,
                              val getServiceOrderByIdUseCase: GetServiceOrderByIdUseCase) {

    @PostMapping("/")
    fun create(@AuthenticationPrincipal principal: Principal, @RequestBody(required = true) serviceOrder: ServiceOrderRequest.Builder) =
            createServiceOrderUseCase.execute(
                    serviceOrder.authentication(principal as Authentication).build()
            )

    @GetMapping("/{orderId}")
    fun getServiceOrderById(@AuthenticationPrincipal principal: Principal, @PathVariable("orderId", required = true) orderId: String) = getServiceOrderByIdUseCase.execute(FindServiceOrderByUUIDRequest(OrderUUID.fromString(orderId), principal as Authentication))
}