package com.wks.servicemarketplace.serviceproviderservice

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.michaelbull.result.onFailure
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.DefaultAuthentication
import com.wks.servicemarketplace.common.auth.Permissions
import com.wks.servicemarketplace.common.auth.TokenValidator
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.http.ErrorResponse
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.TransactionalOutboxJob
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.TransactionalOutboxJobFactory
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.healthCheckRouting
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.internalCompanyRouting
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.companyRouting
import com.wks.servicemarketplace.serviceproviderservice.config.ApplicationParameters
import com.wks.servicemarketplace.serviceproviderservice.config.DatabaseMigration
import com.wks.servicemarketplace.serviceproviderservice.config.appModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
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
                    Permissions.of(credential.payload.getClaim("permissions").asList(String::class.java))
                ))
            }
            verifier(object: JWTVerifier{
                override fun verify(token: String): DecodedJWT {
                    val validator by inject<TokenValidator>()
                    validator.authenticate(token)
                    return JWT.decode(token)
                }

                override fun verify(jwt: DecodedJWT): DecodedJWT {
                    val validator by inject<TokenValidator>()
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
        companyRouting()
        internalCompanyRouting()
        healthCheckRouting()
    }
}

lateinit var scheduler: Scheduler

fun Application.startOutboxScheduler(){
    val parameters by inject<ApplicationParameters>()
    val transactionalOutboxJobFactory by inject<TransactionalOutboxJobFactory>()

    scheduler = StdSchedulerFactory.getDefaultScheduler()
    scheduler.setJobFactory(transactionalOutboxJobFactory)
    val transactionalOutboxJob = JobBuilder.newJob(TransactionalOutboxJob::class.java)
        .withIdentity("transactionalOutboxJob", "transactionalOutbox")
        .build()

    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("transactionalOutboxTrigger", "transactionalOutbox")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInMilliseconds(parameters.outboxIntervalMillis).repeatForever()
        )
        .build()

    scheduler.scheduleJob(transactionalOutboxJob, trigger)
    scheduler.start()
}

fun Application.stopOutboxScheduler(){
    scheduler.shutdown()
}

fun Application.migration(){
    val migration by inject<DatabaseMigration>()
    migration.migrate()
        .onFailure {
            LOGGER.error("Migration Failed: ${it.message}")
        }
}

fun Application.events(){
    environment.monitor.subscribe(ApplicationStarting) {
        LOGGER.info("Application Starting")
    }

    environment.monitor.subscribe(ApplicationStarted) {
        LOGGER.info("Application Started")
        it.migration()
        it.startOutboxScheduler()
    }

    environment.monitor.subscribe(ApplicationStopPreparing) {
        LOGGER.info("Application Stop Preparing")
    }

    environment.monitor.subscribe(ApplicationStopping) {
        LOGGER.info("Application Stopping")
        it.stopOutboxScheduler()
    }

    environment.monitor.subscribe(ApplicationStopped) {
        LOGGER.info("Application Stopped")
    }
}

fun Application.exceptionHandler() {
    install(StatusPages){
        exception<CoreException> {
            val errorResponse = ErrorResponse(it.errorType, it.message, it.details)
            if (call.request.acceptsProtocolBuffers()){
                call.respondBytes(
                        contentType= ContentType.Application.ProtoBuf,
                        status= HttpStatusCode.fromValue(errorResponse.code),
                        bytes = errorResponse.toProtocolBuffer().toByteArray()
                )
            } else call.respond(HttpStatusCode.fromValue(it.errorType.code), errorResponse)
        }
    }
}

data class DefaultPrincipal(val value: DefaultAuthentication): Principal

fun ApplicationRequest.acceptsProtocolBuffers() = this.accept()?.contains(ContentType.Application.ProtoBuf.contentType) ?: false

fun ErrorResponse.toProtocolBuffer(): com.wks.servicemarketplace.api.proto.ErrorResponse =
        com.wks.servicemarketplace.api.proto.ErrorResponse.newBuilder()
        .setErrorType(com.wks.servicemarketplace.api.proto.ErrorResponse.ErrorType.valueOf(this.type.name))
        .setCode(this.code)
        .setMessage(this.message)
        .putAllInfo(this.info)
        .build()