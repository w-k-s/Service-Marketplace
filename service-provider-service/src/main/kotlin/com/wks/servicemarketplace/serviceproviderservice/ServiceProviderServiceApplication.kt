package com.wks.servicemarketplace.serviceproviderservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.wks.servicemarketplace.authservice.adapters.web.resources.HealthResource
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.TokenValidator
import com.wks.servicemarketplace.serviceproviderservice.adapters.web.resources.GraphQLResource
import com.wks.servicemarketplace.serviceproviderservice.config.*
import graphql.GraphQL
import org.glassfish.hk2.api.Immediate
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import javax.ws.rs.core.UriBuilder

class ServiceProviderServiceApplication : ResourceConfig() {

    companion object {
        val LOGGER = LoggerFactory.getLogger(ServiceProviderServiceApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                ServiceProviderServiceApplication().run()
            } catch (e: Exception) {
                LOGGER.error("Application execution failed", e)
                throw e
            }
        }
    }

    init {
        registerResources()
    }

    private fun registerResources() {
        register(ImmediateFeature::class.java)
        register(ObjectMapperProvider::class.java)
        register(AuthenticationFilter::class.java)
        register(object : AbstractBinder() {
            override fun configure() {
                bindFactory(ApplicationParametersFactory::class.java, Immediate::class.java).to(ApplicationParameters::class.java).`in`(Immediate::class.java)
                bindFactory(GraphQLFactory::class.java, Immediate::class.java).to(GraphQL::class.java).`in`(Immediate::class.java)
                bindFactory(ObjectMapperFactory::class.java, Immediate::class.java).to(ObjectMapper::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpConnectionFactory::class.java, Immediate::class.java).to(Connection::class.java).`in`(Immediate::class.java)
                bindFactory(AmqpChannelFactory::class.java, Immediate::class.java).to(Channel::class.java).`in`(Immediate::class.java)
                bindFactory(TokenValidatorFactory::class.java, Immediate::class.java).to(TokenValidator::class.java).`in`(Immediate::class.java)
            }
        })
        register(HealthResource::class.java)
        register(GraphQLResource::class.java)
    }

    fun run() {
        val server = UriBuilder
                .fromUri(System.getenv("serverHost"))
                .port(Integer.parseInt(System.getenv("serverPort")))
                .build()
                .let { JettyHttpContainerFactory.createServer(it, this) }
                .also { LOGGER.info("Started listening on {}:{}", it.uri.host, it.uri.port) }

        try {
            server.join()
        } finally {
            server.destroy()
        }
    }
}