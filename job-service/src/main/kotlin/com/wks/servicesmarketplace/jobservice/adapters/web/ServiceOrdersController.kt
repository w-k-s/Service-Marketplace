package com.wks.servicesmarketplace.jobservice.adapters.web

import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.CreateServiceOrderUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders/v1/")
class ServiceOrdersController(val createServiceOrderUseCase: CreateServiceOrderUseCase){

    @PostMapping("/")
    fun create(@RequestBody serviceOrder: ServiceOrderRequest) = createServiceOrderUseCase.execute(serviceOrder)
}