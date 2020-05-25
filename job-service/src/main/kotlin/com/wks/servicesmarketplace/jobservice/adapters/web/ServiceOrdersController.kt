package com.wks.servicesmarketplace.jobservice.adapters.web

import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.CreateServiceOrderUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderRequest
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.VerifyServiceOrderUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders/v1/")
class ServiceOrdersController(val createServiceOrderUseCase: CreateServiceOrderUseCase,
                              val verifyServiceOrderUseCase: VerifyServiceOrderUseCase){

    @PostMapping("/")
    fun create(@RequestBody serviceOrder: ServiceOrderRequest) = createServiceOrderUseCase.execute(serviceOrder)

    @PostMapping("/{orderId}/verify")
    fun verify(@PathVariable("orderId") orderId: String) = verifyServiceOrderUseCase.execute(orderId)
}