package com.wks.servicesmarketplace.orderservice

import liquibase.Liquibase
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import java.time.Clock
import java.time.OffsetDateTime

@SpringBootApplication
class OrderServiceApplication {

    companion object {
        val LOGGER = LoggerFactory.getLogger(OrderServiceApplication::class.java)
    }

    @Bean
	@Profile("!test")
    fun migrate(liquibase: Liquibase) = CommandLineRunner {
        LOGGER.info("Initializing Liquibase Migrations")

        liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}")

        LOGGER.info("Migration complete")
    }
}

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
