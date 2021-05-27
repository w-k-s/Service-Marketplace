package com.wks.servicesmarketplace.orderservice.config

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class LiquibaseConfiguration {

    @Bean
    fun liquibase(dataSource: DataSource)
        = SpringLiquibase().also{
            it.changeLog = "classpath:liquibase/orderService.changelog.xml"
            it.dataSource = dataSource
        }

}