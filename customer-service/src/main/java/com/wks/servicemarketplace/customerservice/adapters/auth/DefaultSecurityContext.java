package com.wks.servicemarketplace.customerservice.adapters.auth;

import com.wks.servicemarketplace.common.auth.Authentication;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class DefaultSecurityContext implements SecurityContext {

    private final Authentication authentication;
    private final boolean isHttps;
    private final String authorizationScheme;

    public DefaultSecurityContext(Authentication authentication, boolean isHttps, String authorizationScheme) {

        this.authentication = authentication;
        this.isHttps = isHttps;
        this.authorizationScheme = authorizationScheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return authentication;
    }

    @Override
    public boolean isUserInRole(String role) {
        return authentication.hasRole(role);
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
                "user=" + authentication +
                ", isHttps=" + isHttps +
                ", authorizationScheme='" + authorizationScheme + '\'' +
                '}';
    }
}
