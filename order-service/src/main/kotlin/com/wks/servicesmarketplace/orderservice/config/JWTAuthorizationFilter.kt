package com.wks.servicesmarketplace.orderservice.config

import com.wks.servicesmarketplace.orderservice.adapters.auth.TokenValidator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthorizationFilter(authenticationManager: AuthenticationManager,
                             private val tokenValidator: TokenValidator) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer ") }
                ?.substring("Bearer ".length)

        token?.let { tokenValidator.authenticate(it) }
                ?.also { SecurityContextHolder.getContext().authentication = it as Authentication }

        chain.doFilter(request, response)
    }
}