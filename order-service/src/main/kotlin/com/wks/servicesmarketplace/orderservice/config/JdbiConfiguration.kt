package com.wks.servicesmarketplace.orderservice.config

import org.jdbi.v3.core.Jdbi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class JdbiConfiguration {

    @Bean
    fun jdbi(dataSource: DataSource) = Jdbi.create(dataSource)


}