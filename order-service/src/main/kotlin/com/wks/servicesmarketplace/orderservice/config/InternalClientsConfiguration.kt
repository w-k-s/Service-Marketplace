package com.wks.servicesmarketplace.orderservice.config

import com.wks.servicemarketplace.api.InternalServiceProviderClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InternalClientsConfiguration {

    @Bean
    // TODO: Consider using authentication for internal services too.
    fun internalServiceProviderClient(@Value("\${application.service-provider-api.base-url}") baseUrl: String)
        = InternalServiceProviderClient(baseUrl)
}