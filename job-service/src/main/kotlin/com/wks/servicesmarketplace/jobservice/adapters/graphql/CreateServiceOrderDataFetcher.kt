package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.core.auth.User
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.CreateServiceOrderUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.OrderIdResponse
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderRequest
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class CreateServiceOrderDataFetcher(val createServiceOrderUseCase: CreateServiceOrderUseCase) : DataFetcher<OrderIdResponse> {

    override fun get(environment: DataFetchingEnvironment): OrderIdResponse {
        val order = environment.getArgument<Map<String,Any>>("order")
        val user = environment.getContext<User>()
        return createServiceOrderUseCase.execute(ServiceOrderRequest(
                (order["customerExternalId"] as Int).toLong(),
                (order["addressExternalId"] as Int).toLong(),
                (order["serviceCategoryId"] as Int).toLong(),
                order["title"] as String,
                order["description"] as String,
                order["orderDateTime"] as String,
                user
        ))
    }
}