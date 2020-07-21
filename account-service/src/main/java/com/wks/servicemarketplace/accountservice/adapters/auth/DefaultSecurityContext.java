package com.wks.servicemarketplace.accountservice.adapters.auth;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class DefaultSecurityContext implements SecurityContext {

    private final User user;
    private final boolean isHttps;
    private final String authorizationScheme;

    public DefaultSecurityContext(User user, boolean isHttps, String authorizationScheme) {

        this.user = user;
        this.isHttps = isHttps;
        this.authorizationScheme = authorizationScheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return user.hasRole(role);
    }

    @Override
    public boolean isSecure() {
        return isHttps;
    }

    @Override
    public String getAuthenticationScheme() {
        return authorizationScheme;
    }

    @Override
    public String toString() {
        return "DefaultSecurityContext{" +
                "user=" + user +
                ", isHttps=" + isHttps +
                ", authorizationScheme='" + authorizationScheme + '\'' +
                '}';
    }
}
