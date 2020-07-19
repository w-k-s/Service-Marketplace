package com.wks.servicemarketplace.authservice

import com.wks.servicemarketplace.authservice.adapters.keycloak.KeycloakAdapter
import com.wks.servicemarketplace.authservice.adapters.web.resources.AuthResource
import com.wks.servicemarketplace.authservice.config.ObjectMapperProvider
import com.wks.servicemarketplace.authservice.core.IAMAdapter
import org.glassfish.jersey.internal.inject.AbstractBinder
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
                bind(KeycloakAdapter::class.java).to(IAMAdapter::class.java)
            }
        })
        register(ObjectMapperProvider::class.java)
        register(AuthResource::class.java)
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