package com.wks.servicesmarketplace.orderservice.config

import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.common.auth.TokenValidator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager,
                             private val tokenValidator: TokenValidator
) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        request.getHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer ") }
                ?.substring("Bearer ".length)
                ?.let { tokenValidator.authenticate(it) }
                ?.apply {
                    SecurityContextHolder.getContext().authentication = SpringAuthentication(this)
                }
        chain.doFilter(request, response)
    }
}

// TODO Look for a better way to do this.
class SpringAuthentication(private val authentication: com.wks.servicemarketplace.common.auth.Authentication)
    : Authentication, com.wks.servicemarketplace.common.auth.Authentication{
    override fun getName() = this.authentication.name
    override fun getAuthorities() = this.permissions.map { SimpleGrantedAuthority(it.value) }.toList()
    override fun getCredentials() = this.authentication
    override fun getDetails() = this.authentication
    override fun getPrincipal() = this.authentication
    override fun isAuthenticated() = true
    override fun setAuthenticated(isAuthenticated: Boolean) {}
    override val permissions get() = this.authentication.permissions
    override val userId get() = this.authentication.userId
    override fun checkRole(role: Permission) = this.authentication.checkRole(role)
    override fun hasRole(role: Permission) = this.authentication.hasRole(role)
}