package com.wks.servicemarketplace.authservice.config

import org.slf4j.LoggerFactory
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

@Provider
class LoggingFilter : ContainerRequestFilter, ContainerResponseFilter {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoggingFilter::class.java)
    }

    override fun filter(requestContext: ContainerRequestContext?) {
        requestContext?.let {
            LOGGER.info("--> ${it.method} ${it.uriInfo.path}")
        }
    }

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
        requestContext?.let { req ->
            responseContext?.let { resp ->
                LOGGER.info("<-- ${req.method} ${req.uriInfo.path} - ${resp.status}  ${resp.statusInfo.reasonPhrase}")
            }
        }
    }
}