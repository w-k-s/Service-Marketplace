package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.queries.GetServiceOrderByIdQuery
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.GetServiceOrderByIdUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderResponse
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class QueryResolver(private val getServiceOrderByIdUseCase: GetServiceOrderByIdUseCase) : GraphQLQueryResolver {
    fun getServiceOrderById(orderId: String): ServiceOrderResponse {
        try {
            return getServiceOrderByIdUseCase.execute(GetServiceOrderByIdQuery(orderId));
        } catch (e: Exception) {
            throw e.toGraphQLException()
        }
    }
}