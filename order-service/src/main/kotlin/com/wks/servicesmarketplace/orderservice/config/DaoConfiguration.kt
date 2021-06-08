package com.wks.servicesmarketplace.orderservice.config

import DefaultServiceOrderDao
import com.wks.servicesmarketplace.orderservice.adapters.dao.DefaultBidDao
import com.wks.servicesmarketplace.orderservice.core.ServiceOrderDao
import org.jdbi.v3.core.Jdbi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DaoConfiguration {

    @Bean
    fun serviceOrderDao(jdbi: Jdbi) = DefaultServiceOrderDao(jdbi)

    @Bean
    fun bidDao(jdbi: Jdbi) = DefaultBidDao(jdbi)
}