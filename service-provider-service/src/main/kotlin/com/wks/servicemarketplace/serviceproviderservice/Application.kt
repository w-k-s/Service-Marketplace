package com.wks.servicemarketplace.serviceproviderservice

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.DefaultAuthentication
import com.wks.servicemarketplace.common.auth.StandardTokenValidator
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.TransactionalOutboxScheduler
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.healthCheckRouting
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.serviceProviderRouting
import com.wks.servicemarketplace.serviceproviderservice.config.ApplicationParameters
import com.wks.servicemarketplace.serviceproviderservice.config.DatabaseMigration
import com.wks.servicemarketplace.serviceproviderservice.config.appModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.routing.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("Application")

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CallLogging)
    install(Locations)
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    this.authentication()
    this.contentNegotiation()
    this.routing()
    this.events()
}

fun Application.authentication(){
    install(Authentication){
        jwt(name="standardJwtToken"){
            realm = "ServiceMarketplace"
            validate { credential ->
                DefaultPrincipal(DefaultAuthentication(
                    UserId.fromString(credential.payload.getClaim("userId").asString()),
                    credential.payload.subject,
                    credential.payload.getClaim("permissions").asList(String::class.java)
                ))
            }
            verifier(object: JWTVerifier{
                override fun verify(token: String): DecodedJWT {
                    val validator by inject<StandardTokenValidator>()
                    validator.authenticate(token)
                    return JWT.decode(token)
                }

                override fun verify(jwt: DecodedJWT): DecodedJWT {
                    val validator by inject<StandardTokenValidator>()
                    validator.authenticate(jwt.token)
                    return jwt
                }
            }
            )
        }
    }
}

fun Application.contentNegotiation(){
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

fun Application.routing(){
    routing {
        serviceProviderRouting()
        healthCheckRouting()
    }
}

fun Application.events(){
    environment.monitor.subscribe(ApplicationStarting) {
        LOGGER.info("Application Starting")
    }

    environment.monitor.subscribe(ApplicationStarted) {
        LOGGER.info("Application Started")
        val migration by inject<DatabaseMigration>()
        migration.migrate()

        val transactionalOutbox by inject<TransactionalOutboxScheduler>()
        val parameters by inject<ApplicationParameters>()
        transactionalOutbox.scheduleExecution(parameters.outboxIntervalMillis)
    }

    environment.monitor.subscribe(ApplicationStopPreparing) {
        LOGGER.info("Application Stop Preparing")
        val transactionalOutbox by inject<TransactionalOutboxScheduler>()
        transactionalOutbox.stop()
    }

    environment.monitor.subscribe(ApplicationStopping) {
        LOGGER.info("Application Stopping")
    }

    environment.monitor.subscribe(ApplicationStopped) {
        LOGGER.info("Application Stopped")
    }
}

data class DefaultPrincipal(val value: DefaultAuthentication): Principal