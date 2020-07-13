package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.CreateServiceOrderUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.OrderIdResponse
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderRequest
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class CreateServiceOrderDataFetcher(val createServiceOrderUseCase: CreateServiceOrderUseCase,
                                    val argumentMapper: DataFetchingEnvironmentMapper) : DataFetcher<OrderIdResponse> {

    override fun get(environment: DataFetchingEnvironment): OrderIdResponse {
        val request = argumentMapper.getArgument(environment, "order", ServiceOrderRequest::class.java)
        return createServiceOrderUseCase.execute(request)
    }
}