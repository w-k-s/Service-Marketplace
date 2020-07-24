package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.adapters.auth.DefaultSecurityContext;
import com.wks.servicemarketplace.accountservice.adapters.auth.TokenValidator;
import com.wks.servicemarketplace.accountservice.core.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;

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

        final User user = tokenValidator.getUserIfValid(token);
        requestContext.setSecurityContext(new DefaultSecurityContext(
                user,
                false,
                "Bearer"
        ));
        LOGGER.info("Security Context set for user: '{}'", user.getUsername());
    }
}
