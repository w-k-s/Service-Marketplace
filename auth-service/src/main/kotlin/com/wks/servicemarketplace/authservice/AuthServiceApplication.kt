package com.wks.servicemarketplace.authservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.wks.servicemarketplace.authservice.adapters.auth.fusionauth.AssignGroupRetrier
import com.wks.servicemarketplace.authservice.adapters.auth.fusionauth.FusionAuthAdapter
import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import com.wks.servicemarketplace.authservice.adapters.db.dao.DefaultEventDao
import com.wks.servicemarketplace.authservice.adapters.db.dao.DefaultOutboxDao
import com.wks.servicemarketplace.authservice.adapters.db.dao.DefaultSagaDao
import com.wks.servicemarketplace.authservice.adapters.events.DefaultMessagePublisher
import com.wks.servicemarketplace.authservice.adapters.events.DefaultEventReceiver
import com.wks.servicemarketplace.authservice.adapters.events.TransactionalOutboxJobFactory
import com.wks.servicemarketplace.authservice.adapters.web.resources.ApiResource
import com.wks.servicemarketplace.authservice.adapters.web.resources.DefaultExceptionMapper
import com.wks.servicemarketplace.authservice.adapters.web.resources.HealthResource
import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.authservice.config.*
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.sagas.CreateProfileSaga
import com.wks.servicemarketplace.common.auth.TokenValidator
import org.glassfish.hk2.api.Immediate
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED
import org.quartz.Scheduler
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.security.PublicKey
import javax.ws.rs.core.UriBuilder

class AuthServiceApplication(private val parameters : ApplicationParameters = ApplicationParameters.load()) : ResourceConfig() {

    companion object {
        val LOGGER = LoggerFactory.getLogger(AuthServiceApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val application = AuthServiceApplication()
                application.run()
            } catch (e: Exception) {
                LOGGER.error("Application execution failed", e)
                throw e
            }
        }
    }

    init {
        properties = mapOf(PROCESSING_RESPONSE_ERRORS_ENABLED to true)
        registerResources()
    }

    private fun registerResources() {
        register(ImmediateFeature::class.java)
        register(object : AbstractBinder() {
            override fun configure() {
                bindFactory(FusionAuthConfigurationFactory::class.java, Immediate::class.java).to(FusionAuthConfiguration::class.java).`in`(Immediate::class.java)
                bindFactory(ObjectMapperFactory::class.java, Immediate::class.java).to(ObjectMapper::class.java).`in`(Immediate::class.java)
                bindFactory(PrivateKeyFactory::class.java, Immediate::class.java).to(PrivateKey::class.java).`in`(Immediate::class.java)
                bindFactory(PublicKeyFactory::class.java, Immediate::class.java).to(PublicKey::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpConnectionFactory::class.java, Immediate::class.java).to(Connection::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpChannelFactory::class.java, Immediate::class.java).to(Channel::class.java).`in`(Immediate::class.java)
                bindFactory(TransactionalOutboxSchedulerFactory::class.java).to(Scheduler::class.java)
                bindFactory(DataSourceFactory::class.java, Immediate::class.java).to(DataSource::class.java).`in`(Immediate::class.java)
                bindFactory(ClientCredentialsTokenSupplierFactory::class.java, Immediate::class.java).to(ClientCredentialsTokenSupplier::class.java).`in`(Immediate::class.java)
                bindFactory(TokenValidatorFactory::class.java, Immediate::class.java).to(TokenValidator::class.java).`in`(Immediate::class.java)

                bind(parameters).to(ApplicationParameters::class.java)
                bind(TransactionalOutboxJobFactory::class.java).to(TransactionalOutboxJobFactory::class.java).`in`(Immediate::class.java)
                bind(DefaultEventDao::class.java).to(EventDao::class.java).`in`(Immediate::class.java)
                bind(DefaultOutboxDao::class.java).to(OutboxDao::class.java).`in`(Immediate::class.java)
                bind(DefaultSagaDao::class.java).to(SagaDao::class.java).`in`(Immediate::class.java)
                bind(FusionAuthAdapter::class.java).to(IAMAdapter::class.java).`in`(Immediate::class.java)
                bind(TokenService::class.java).to(TokenService::class.java).`in`(Immediate::class.java)
                bind(DefaultEventReceiver::class.java).to(DefaultEventReceiver::class.java).`in`(Immediate::class.java)
                bind(DefaultMessagePublisher::class.java).to(DefaultMessagePublisher::class.java).`in`(Immediate::class.java)
                bind(CreateProfileSaga::class.java).to(CreateProfileSaga::class.java).`in`(Immediate::class.java)
                bind(AssignGroupRetrier::class.java).to(AssignGroupRetrier::class.java).`in`(Immediate::class.java)
                bind(DatabaseMigration::class.java).to(DatabaseMigration::class.java).`in`(Immediate::class.java)
            }
        })
        register(DefaultExceptionMapper::class.java)
        register(ObjectMapperProvider::class.java)
        register(DefaultApplicationEventListener::class.java)
        register(ApiResource::class.java)
        register(HealthResource::class.java)
    }

    fun run() {
        val uri = UriBuilder
                .fromUri(parameters.serverHost)
                .port(parameters.serverPort)
                .build()

        val server = JettyHttpContainerFactory.createServer(uri, this)
        try {
            LOGGER.info("Started listening on {}:{}", uri.host, uri.port)
            server.join()
        } finally {
            server.destroy()
        }
    }
}