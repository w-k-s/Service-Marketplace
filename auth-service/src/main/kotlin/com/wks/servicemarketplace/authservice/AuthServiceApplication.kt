package com.wks.servicemarketplace.authservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.adapters.fusionauth.FusionAuthAdapter
import com.wks.servicemarketplace.authservice.adapters.graphql.LoginDataFetcher
import com.wks.servicemarketplace.authservice.adapters.graphql.RegisterDataFetcher
import com.wks.servicemarketplace.authservice.adapters.web.resources.GraphQLResource
import com.wks.servicemarketplace.authservice.config.*
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import com.wks.servicemarketplace.authservice.core.iam.TokenService
import graphql.GraphQL
import org.glassfish.hk2.api.Immediate
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import java.security.PrivateKey
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
                throw e
            }
        }
    }

    init {
        registerResources()
    }

    private fun registerResources() {
        register(ImmediateFeature::class.java)
        register(object : AbstractBinder() {
            override fun configure() {
                bindFactory(ApplicationParametersFactory::class.java, Immediate::class.java).to(ApplicationParameters::class.java).`in`(Immediate::class.java)
                bindFactory(FusionAuthConfigurationFactory::class.java, Immediate::class.java).to(FusionAuthConfiguration::class.java).`in`(Immediate::class.java)
                bindFactory(GraphQLFactory::class.java, Immediate::class.java).to(GraphQL::class.java).`in`(Immediate::class.java)
                bindFactory(ObjectMapperFactory::class.java, Immediate::class.java).to(ObjectMapper::class.java).`in`(Immediate::class.java)
                bindFactory(PrivateKeyFactory::class.java, Immediate::class.java).to(PrivateKey::class.java).`in`(Immediate::class.java)
                bind(FusionAuthAdapter::class.java).to(IAMAdapter::class.java).`in`(Immediate::class.java)
                bind(LoginDataFetcher::class.java).to(LoginDataFetcher::class.java)
                bind(RegisterDataFetcher::class.java).to(RegisterDataFetcher::class.java)
                bind(TokenService::class.java).to(TokenService::class.java)
            }
        })
        register(ObjectMapperProvider::class.java)
        register(GraphQLResource::class.java)
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