package com.wks.servicesmarketplace.orderservice.adapters.web

import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.CreateQuoteRequest
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderRequest
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.annotation.security.RolesAllowed
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/orders")
class ServiceOrdersController(val serviceOrderService: ServiceOrderService) {

    @PostMapping("/")
    @RolesAllowed("order.create")
    fun createOrder(@AuthenticationPrincipal principal: Principal, @Valid @RequestBody(required = true) serviceOrderRequest: ServiceOrderRequest)
    = serviceOrderService.createOrder(serviceOrderRequest, principal as Authentication)

    @GetMapping("/{orderId}")
    fun getServiceOrderById(@AuthenticationPrincipal principal: Principal, @PathVariable("orderId", required = true) orderId: String)
    = serviceOrderService.getOrder(OrderUUID.fromString(orderId), principal as Authentication)

    @PostMapping("/{orderId}/bid")
    @RolesAllowed("bid.create")
    fun createQuote(@AuthenticationPrincipal principal: Principal,
                    @PathVariable("orderId", required = true) orderId: String,
                    @Valid @RequestBody(required = true) createQuoteRequest: CreateQuoteRequest)
    = serviceOrderService.createOrUpdateQuote(createQuoteRequest, OrderUUID.fromString(orderId), principal as Authentication)
}