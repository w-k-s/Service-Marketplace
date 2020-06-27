package com.wks.servicesmarketplace.jobservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariDataSource
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource

@Configuration
class SagaConfiguration {

    @Autowired
    private lateinit var environment: Environment

    @Bean
    fun sagaStore(objectMapper: ObjectMapper, entityManagerProvider: EntityManagerProvider) =
            JpaSagaStore.builder()
                    .serializer(JacksonSerializer.builder().objectMapper(objectMapper).build())
                    .entityManagerProvider(entityManagerProvider)
                    .build()
}