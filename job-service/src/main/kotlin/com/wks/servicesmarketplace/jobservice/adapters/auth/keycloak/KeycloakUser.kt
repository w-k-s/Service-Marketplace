package com.wks.servicesmarketplace.jobservice.adapters.auth.keycloak

import com.wks.servicesmarketplace.jobservice.core.auth.Role
import com.wks.servicesmarketplace.jobservice.core.auth.User
import org.springframework.security.core.GrantedAuthority

data class KeycloakRole(override val name: String) : Role, GrantedAuthority {
    override fun getAuthority(): String {
        return name
    }
}

data class KeycloakUser(
        override val uuid: String,
        val scope: String? = null,
        val email: String,
        val emailVerified: Boolean,
        private val name: String,
        val preferredUsername: String,
        val givenName: String?,
        val familyName: String?,
        override val roles: List<KeycloakRole>
) : User {
    override fun getName() = this.name
    override val username = this.preferredUsername
}