package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.common.auth.Authentication;
import com.wks.servicemarketplace.common.auth.TokenValidator;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.customerservice.adapters.auth.DefaultSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Inject
    private TokenValidator tokenValidator;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String authorization = requestContext.getHeaderString("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            LOGGER.info("authorization token not found");
            return;
        }

        final String token = authorization.substring("Bearer".length()).trim();

        Authentication authentication = null;
        try {
            authentication = tokenValidator.authenticate(token);
        } catch (CoreException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
            return;
        }

        requestContext.setSecurityContext(new DefaultSecurityContext(
                authentication,
                false,
                "Bearer"
        ));
        LOGGER.info("Security Context set for user: '{}'", authentication.getName());
    }
}
