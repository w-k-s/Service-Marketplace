package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.DefaultSecurityContext
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.InvalidTokenException
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.TokenValidator
import org.ietf.jgss.GSSException.UNAUTHORIZED
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response

class AuthenticationFilter @Inject constructor(private val tokenValidator: TokenValidator) : ContainerRequestFilter {

    private val LOGGER: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun filter(requestContext: ContainerRequestContext) {
        try {
            requestContext.getHeaderString("Authorization")
                    ?.takeIf { it.isNotBlank() }
                    ?.substring("Bearer ".length)
                    ?.let {
                        tokenValidator.authenticate(it)
                    }?.also {
                        requestContext.securityContext = DefaultSecurityContext(it, false, "Bearer")
                        LOGGER.info("Security Context set for user: '{}'", it.name)
                    } ?: LOGGER.info("authorization token not found")
        } catch (e: InvalidTokenException) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).entity(e.message).build())
        }
    }
}
