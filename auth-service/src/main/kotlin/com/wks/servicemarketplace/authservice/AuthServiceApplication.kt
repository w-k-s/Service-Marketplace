package com.wks.servicemarketplace.authservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.wks.servicemarketplace.authservice.adapters.auth.fusionauth.AssignGroupRetrier
import com.wks.servicemarketplace.authservice.adapters.auth.fusionauth.FusionAuthAdapter
import com.wks.servicemarketplace.authservice.adapters.events.DefaultEventPublisher
import com.wks.servicemarketplace.authservice.adapters.events.DefaultEventReceiver
import com.wks.servicemarketplace.authservice.adapters.web.resources.ApiResource
import com.wks.servicemarketplace.authservice.adapters.web.resources.DefaultExceptionMapper
import com.wks.servicemarketplace.authservice.adapters.web.resources.HealthResource
import com.wks.servicemarketplace.authservice.config.*
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import org.glassfish.hk2.api.Immediate
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.security.PublicKey
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.ExceptionMapper

class AuthServiceApplication : ResourceConfig() {

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
                bindFactory(ApplicationParametersFactory::class.java, Immediate::class.java).to(ApplicationParameters::class.java).`in`(Immediate::class.java)
                bindFactory(FusionAuthConfigurationFactory::class.java, Immediate::class.java).to(FusionAuthConfiguration::class.java).`in`(Immediate::class.java)
                bindFactory(ObjectMapperFactory::class.java, Immediate::class.java).to(ObjectMapper::class.java).`in`(Immediate::class.java)
                bindFactory(PrivateKeyFactory::class.java, Immediate::class.java).to(PrivateKey::class.java).`in`(Immediate::class.java)
                bindFactory(PublicKeyFactory::class.java, Immediate::class.java).to(PublicKey::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpConnectionFactory::class.java, Immediate::class.java).to(Connection::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpChannelFactory::class.java, Immediate::class.java).to(Channel::class.java).`in`(Immediate::class.java)

                bind(DefaultEventPublisher::class.java).to(EventPublisher::class.java).`in`(Immediate::class.java)
                bind(FusionAuthAdapter::class.java).to(IAMAdapter::class.java).`in`(Immediate::class.java)
                bind(TokenService::class.java).to(TokenService::class.java).`in`(Immediate::class.java)
                bind(DefaultEventReceiver::class.java).to(DefaultEventReceiver::class.java).`in`(Immediate::class.java)
                bind(AssignGroupRetrier::class.java).to(AssignGroupRetrier::class.java).`in`(Immediate::class.java)
            }
        })
        register(DefaultExceptionMapper::class.java)
        register(ObjectMapperProvider::class.java)
        register(ApiResource::class.java)
        register(HealthResource::class.java)
    }

    fun run() {
        val uri = UriBuilder
                .fromUri(System.getenv("serverHost"))
                .port(Integer.parseInt(System.getenv("serverPort")))
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