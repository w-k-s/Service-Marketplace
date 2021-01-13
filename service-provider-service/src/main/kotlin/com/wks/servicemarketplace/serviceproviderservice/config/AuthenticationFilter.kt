package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.common.auth.TokenValidator
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.http.httpStatusCode
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.DefaultSecurityContext
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
        } catch (e: CoreException) {
            requestContext.abortWith(Response.status(e.errorType.httpStatusCode()).entity(e.message).build())
        }
    }
}
