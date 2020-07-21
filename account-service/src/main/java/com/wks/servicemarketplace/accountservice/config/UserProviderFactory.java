package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.adapters.auth.DefaultSecurityContextUserProvider;
import com.wks.servicemarketplace.accountservice.core.auth.UserProvider;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;

public class UserProviderFactory implements Factory<UserProvider> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProviderFactory.class);

    private SecurityContext securityContext;

    public UserProviderFactory(@Context SecurityContext securityContext) {
        LOGGER.info("Creating UserProviderFactory with securityContext for '{}'",
                Optional.ofNullable(securityContext)
                        .map(SecurityContext::getUserPrincipal)
                        .map(Principal::getName)
                        .orElse(null));
        this.securityContext = securityContext;
    }

    @Override
    public UserProvider provide() {
        return new DefaultSecurityContextUserProvider(securityContext);
    }

    @Override
    public void dispose(UserProvider instance) {

    }
}
