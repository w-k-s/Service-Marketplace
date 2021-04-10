package com.wks.servicemarketplace.serviceproviderservice.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.michaelbull.result.expect
import com.rabbitmq.client.ConnectionFactory
import com.wks.servicemarketplace.authservice.api.ClientCredentialsRequest
import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.common.auth.StandardTokenValidator
import com.wks.servicemarketplace.common.auth.TokenValidator
import com.wks.servicemarketplace.common.readPublicKey
import com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao.*
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.DefaultEventReceiver
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.DefaultMessagePublisher
import com.wks.servicemarketplace.serviceproviderservice.adapters.events.TransactionalOutboxScheduler
import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyRepresentativeUseCase
import com.wks.servicemarketplace.serviceproviderservice.core.usecase.CreateCompanyUseCase
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.stream.Collectors

val appModule = module {
    val applicationParameters = ApplicationParameters.load()
    single(createdAtStart = true) {
        ApplicationParameters.load()
    }
    single(createdAtStart = true){
        DataSource(
            applicationParameters.jdbcUrl,
            applicationParameters.jdbcUsername,
            applicationParameters.jdbcPassword
        )
    }
    single(createdAtStart = true) {
        val connectionFactory = ConnectionFactory().also {
            it.setUri(applicationParameters.amqpUri)
        }.newConnection()
        connectionFactory.createChannel()
    }
    single(createdAtStart = true){
        DefaultEventReceiver(get(),get(),get(),get())
    }
    single(createdAtStart = true){
        this.javaClass.classLoader.getResourceAsStream("publicKey.pem").readPublicKey()
            .expect { "Failed to read public key" }
    }
    single<TokenValidator>{
        StandardTokenValidator(
            publicKey = get(),
            objectMapper = get()
        )
    }
    single{
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
    single{ DefaultMessagePublisher(
        channel = get()
    ) }
    single{ ClientCredentialsTokenSupplier(
        clientCredentials = ClientCredentialsRequest(applicationParameters.clientId, applicationParameters.clientSecret),
        authServiceBaseUrl = applicationParameters.authServiceBaseUrl,
        objectMapper = get()
    ) }
    single{ DatabaseMigration(
        dataSource = get()
    ) }
    single { CreateCompanyUseCase(
        companyDao = get(),
        companyRepresentativeDao = get(),
        employeeDao = get(),
        eventDao = get(),
        outboxDao = get(),
        objectMapper = get()
    )}
    single {
        CreateCompanyRepresentativeUseCase(
            companyRepresentativeDao=get(),
            eventDao = get(),
            outboxDao = get(),
            objectMapper = get()
        )
    }
    single(createdAtStart = true){
        TransactionalOutboxScheduler(
            outboxDao = get(),
            messagePublisher = get(),
            clientCredentialsTokenSupplier = get()
        )
    }
    singleBy<CompanyDao, DefaultCompanyDao>()
    singleBy<CompanyRepresentativeDao, DefaultCompanyRepresentativeDao>()
    singleBy<EmployeeDao, DefaultEmployeeDao>()
    singleBy<EventDao, DefaultEventDao>()
    singleBy<OutboxDao, DefaultOutboxDao>()
}
