package com.wks.servicesmarketplace.orderservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.common.auth.StandardTokenValidator
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.readPublicKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.PublicKey

@Configuration
class TokenValidatorConfiguration {

    @Bean
    fun tokenValidator(objectMapper: ObjectMapper) = StandardTokenValidator(publicKey(), objectMapper)

    private fun publicKey(): PublicKey = javaClass.classLoader.getResourceAsStream("publicKey.pem")?.readPublicKey()
            ?: throw CoreException(ErrorType.RESOURCE_NOT_FOUND, "Public key not found")

}