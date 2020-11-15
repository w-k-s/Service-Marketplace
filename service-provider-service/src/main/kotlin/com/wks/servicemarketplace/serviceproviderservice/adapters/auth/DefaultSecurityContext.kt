package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import java.security.Principal
import javax.ws.rs.core.SecurityContext

class DefaultSecurityContext(private val authentication: Authentication,
                             private val isHttps: Boolean,
                             private val authorizationScheme: String?) : SecurityContext {

    override fun getUserPrincipal(): Principal? {
        return authentication
    }

    override fun isUserInRole(role: String): Boolean {
        return authentication.hasRole(role)
    }

    override fun isSecure(): Boolean {
        return isHttps
    }

    override fun getAuthenticationScheme(): String? {
        return authorizationScheme
    }

    override fun toString(): String {
        return "DefaultSecurityContext{" +
                "user=" + authentication +
                ", isHttps=" + isHttps +
                ", authorizationScheme='" + authorizationScheme + '\'' +
                '}'
    }
}