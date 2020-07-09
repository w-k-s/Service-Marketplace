package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.CreateServiceOrderUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.OrderIdResponse
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderRequest
import org.springframework.stereotype.Component

@Component
class MutationResolver(private val createServiceOrder: CreateServiceOrderUseCase) : GraphQLMutationResolver {

    fun createServiceOrder(serviceOrder: ServiceOrderRequest): OrderIdResponse {
       return createServiceOrder.execute(serviceOrder)
    }
}