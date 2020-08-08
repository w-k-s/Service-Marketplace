package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.core.auth.AuthorizationException
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
        val order = environment.getArgument<Map<String, Any>>("order")
        val user = environment.getContext<User>()
        return createServiceOrderUseCase.execute(ServiceOrderRequest.Builder()
                .customerExternalId((order["customerExternalId"] as Int).toLong())
                .addressExternalId((order["addressExternalId"] as Int).toLong())
                .serviceCategoryId((order["serviceCategoryId"] as Int).toLong())
                .title(order["title"] as String)
                .description(order["description"] as String)
                .orderDateTime(order["orderDateTime"] as String)
                .user(user)
                .build())
    }
}