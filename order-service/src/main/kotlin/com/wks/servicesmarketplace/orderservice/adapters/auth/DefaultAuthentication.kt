package com.wks.servicesmarketplace.orderservice.adapters.auth

import com.wks.servicesmarketplace.orderservice.core.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.auth.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class DefaultAuthentication(override val user: User?,
                            override val token: String,
                            private val name: String,
                            private val permissions: List<String>)
    : AbstractAuthenticationToken(permissions.map { SimpleGrantedAuthority(it) }), Authentication {

    init {
        super.setAuthenticated(true)
    }

    override fun getName() = name
    override val roles: List<String> = permissions

    override fun getCredentials() = token

    override fun getPrincipal() = user

}