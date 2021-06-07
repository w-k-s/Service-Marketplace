package com.wks.servicesmarketplace.orderservice.config

import com.wks.servicemarketplace.api.InternalServiceProviderClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class InternalApiConfiguration {
    // TODO: Use authentication for internal services too.
    fun internalServiceProviderClient(@Value("application.service-provider-api.base-url") baseUrl: String) = InternalServiceProviderClient(baseUrl)
}