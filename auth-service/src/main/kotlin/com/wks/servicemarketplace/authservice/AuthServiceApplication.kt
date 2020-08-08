package com.wks.servicemarketplace.authservice

import com.wks.servicemarketplace.authservice.adapters.graphql.LoginDataFetcher
import com.wks.servicemarketplace.authservice.adapters.graphql.RegisterDataFetcher
import com.wks.servicemarketplace.authservice.adapters.keycloak.KeycloakAdapter
import com.wks.servicemarketplace.authservice.adapters.web.resources.GraphQLResource
import com.wks.servicemarketplace.authservice.config.*
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import graphql.GraphQL
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import javax.ws.rs.core.UriBuilder

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
            }
        }
    }

    init {
        registerResources()
    }

    private fun registerResources() {
        register(object : AbstractBinder() {
            override fun configure() {
                bindFactory(ApplicationParametersFactory::class.java).to(ApplicationParameters::class.java)
                bindFactory(KeycloakConfigurationFactory::class.java).to(KeycloakConfiguration::class.java)
                bindFactory(GraphQLFactory::class.java).to(GraphQL::class.java)
                bindFactory(GraphQLFactory::class.java).to(GraphQL::class.java)
                bind(LoginDataFetcher::class.java).to(LoginDataFetcher::class.java)
                bind(RegisterDataFetcher::class.java).to(RegisterDataFetcher::class.java)
                bind(KeycloakAdapter::class.java).to(IAMAdapter::class.java)
            }
        })
        register(ObjectMapperProvider::class.java)
        register(GraphQLResource::class.java)
    }

    fun run() {
        val uri = UriBuilder
                .fromUri("http://localhost")
                .port("8082".toInt())
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