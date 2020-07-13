package com.wks.servicesmarketplace.jobservice.config

import com.wks.servicesmarketplace.jobservice.adapters.graphql.CreateServiceOrderDataFetcher
import com.wks.servicesmarketplace.jobservice.adapters.graphql.GetServiceOrderByIdDataFetcher
import org.springframework.stereotype.Component

@Component
class GraphQLDataFetchers(val getServiceOrderByIdDataFetcher: GetServiceOrderByIdDataFetcher,
                          val createServiceOrderDataFetcher: CreateServiceOrderDataFetcher) {


}