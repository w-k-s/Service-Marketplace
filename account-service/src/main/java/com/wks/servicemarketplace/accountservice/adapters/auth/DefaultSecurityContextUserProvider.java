package com.wks.servicemarketplace.accountservice.adapters.auth;

import com.wks.servicemarketplace.accountservice.core.auth.User;
import com.wks.servicemarketplace.accountservice.core.auth.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class DefaultSecurityContextUserProvider implements UserProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityContextUserProvider.class);

    private final SecurityContext context;

    public DefaultSecurityContextUserProvider(@Context SecurityContext context) {
        this.context = context;
        LOGGER.info("Creating '{}'", this);
    }

    @Override
    public User getUser() {
        return (User) context.getUserPrincipal();
    }

    @Override
    public String toString() {
        return "DefaultSecurityContextUserProvider{" +
                "context=" + context +
                '}';
    }
}
