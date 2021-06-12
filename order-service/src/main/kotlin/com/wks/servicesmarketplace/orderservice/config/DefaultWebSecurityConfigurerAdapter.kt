package com.wks.servicesmarketplace.orderservice.config

import com.wks.servicemarketplace.common.auth.TokenValidator
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy


@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,  // Enable @PreAuthorize, @PostAuthorize
        proxyTargetClass = true//,
        //jsr250Enabled = true // Enable @RolesAllowed - doesn't work, use prepost instead
)
class DefaultWebSecurityConfigurerAdapter(private val tokenValidator: TokenValidator) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(JWTAuthorizationFilter(authenticationManager(), tokenValidator))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    }

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
        return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
    }
}