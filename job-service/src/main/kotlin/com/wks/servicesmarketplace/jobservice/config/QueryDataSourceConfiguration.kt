package com.wks.servicesmarketplace.jobservice.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "queryEntityManager",
        transactionManagerRef = "queryTransactionManager",
        basePackages = ["com.wks.servicesmarketplace.jobservice.core.repositories"]
)
class QueryDataSourceConfiguration {

    @Autowired
    private lateinit var environment: Environment

    @Bean
    fun queryTransactionManager(@Qualifier("queryEntityManager") queryEntityManager: LocalContainerEntityManagerFactoryBean): PlatformTransactionManager {
        return JpaTransactionManager(queryEntityManager.getObject()!!)
    }

    @Bean
    fun queryEntityManager(builder: EntityManagerFactoryBuilder,
                             @Qualifier("queryDataSource") queryDataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val properties: HashMap<String, Any?> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = environment.getProperty("spring.jpa.hibernate.ddl-auto")
        properties["hibernate.dialect"] = environment.getProperty("spring.jpa.properties.hibernate.dialect")

        return builder
                .dataSource(queryDataSource)
                .persistenceUnit("queryDataSource")
                .properties(properties)
                .packages("com.wks.servicesmarketplace.jobservice.core")
                .build()
    }

    @Bean
    fun queryDataSource(): DataSource {
        return DataSourceBuilder.create()
                .url(environment.getProperty("spring.query.datasource.url"))
                .username(environment.getProperty("spring.query.datasource.username"))
                .password(environment.getProperty("spring.query.datasource.password"))
                .driverClassName(environment.getProperty("spring.datasource.driver-class-name")!!)
                .type(HikariDataSource::class.java)
                .build()
    }
}