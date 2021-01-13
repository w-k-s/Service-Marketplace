package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import java.lang.Exception
import java.security.Principal
import javax.ws.rs.core.SecurityContext

class DefaultSecurityContext(private val authentication: Authentication,
                             private val isHttps: Boolean,
                             private val authorizationScheme: String?) : SecurityContext {

    override fun getUserPrincipal(): Principal? {
        return authentication
    }

    override fun isUserInRole(role: String): Boolean {
        return try {
            authentication.hasRole(Permission.valueOf(role))
        } catch (e: Exception) {
            false
        }
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