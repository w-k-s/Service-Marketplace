package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.core.auth.User;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;

public class UserProviderFactory implements Factory<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProviderFactory.class);

    private User user;

    public UserProviderFactory(@Context SecurityContext securityContext) {
        LOGGER.info("Creating UserProviderFactory with securityContext for '{}'",
                Optional.ofNullable(securityContext)
                        .map(SecurityContext::getUserPrincipal)
                        .map(Principal::getName)
                        .orElse(null));

        this.user = (User) Optional.ofNullable(securityContext)
                .map(SecurityContext::getUserPrincipal)
                .orElse(null);
    }

    @Override
    public User provide() {
        return user;
    }

    @Override
    public void dispose(User instance) {

    }
}
