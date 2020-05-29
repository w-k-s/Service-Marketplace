package com.wks.servicesmarketplace.jobservice.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
class CommandDataSourceConfiguration {
    @Autowired
    private lateinit var environment: Environment

    @Bean
    @Primary
    fun commandEntityManager(builder: EntityManagerFactoryBuilder,
                             @Qualifier("commandDataSource") commandDataSource: DataSource?): LocalContainerEntityManagerFactoryBean {
        val properties: HashMap<String, Any?> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = environment.getProperty("spring.jpa.hibernate.ddl-auto")
        properties["hibernate.dialect"] = environment.getProperty("spring.jpa.properties.hibernate.dialect")

        return builder
                .dataSource(commandDataSource)
                .persistenceUnit("commandDataSource")
                .properties(properties)
                .packages("org.axonframework.eventhandling.tokenstore",
                        "org.axonframework.modelling.saga.repository.jpa",
                        "org.axonframework.eventsourcing.eventstore.jpa")
                .build()
    }


    @Bean
    @Primary
    fun commandDataSource(): DataSource {
        return DataSourceBuilder.create()
                .url(environment.getProperty("spring.command.datasource.url"))
                .username(environment.getProperty("spring.command.datasource.username"))
                .password(environment.getProperty("spring.command.datasource.password"))
                .driverClassName(environment.getProperty("spring.datasource.driver-class-name")!!)
                .type(HikariDataSource::class.java)
                .build()
    }
}