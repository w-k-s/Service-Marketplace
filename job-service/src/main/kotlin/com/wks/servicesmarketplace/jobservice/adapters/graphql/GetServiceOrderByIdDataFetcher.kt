package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.queries.GetServiceOrderByIdQuery
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.GetServiceOrderByIdUseCase
import com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder.ServiceOrderResponse
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class GetServiceOrderByIdDataFetcher(val serviceOrderByIdUseCase: GetServiceOrderByIdUseCase) : DataFetcher<ServiceOrderResponse> {

    override fun get(environment: DataFetchingEnvironment?): ServiceOrderResponse {
        val query = GetServiceOrderByIdQuery(environment!!.getArgument("orderId"))
        return serviceOrderByIdUseCase.execute(query)
    }
}