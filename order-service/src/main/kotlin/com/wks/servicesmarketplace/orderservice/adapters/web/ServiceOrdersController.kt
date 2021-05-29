package com.wks.servicesmarketplace.orderservice.adapters.web

import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.service.dto.ServiceOrderRequest
import com.wks.servicesmarketplace.orderservice.core.service.ServiceOrderService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/orders")
class ServiceOrdersController(val serviceOrderService: ServiceOrderService) {

    @PostMapping("/")
    fun create(@AuthenticationPrincipal principal: Principal, @Valid @RequestBody(required = true) serviceOrderRequest: ServiceOrderRequest)
    = serviceOrderService.create(serviceOrderRequest, principal as Authentication)

    @GetMapping("/{orderId}")
    fun getServiceOrderById(@AuthenticationPrincipal principal: Principal, @PathVariable("orderId", required = true) orderId: String)
    = serviceOrderService.get(OrderUUID.fromString(orderId), principal as Authentication)
}