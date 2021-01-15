package com.wks.servicesmarketplace.orderservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.common.auth.StandardTokenValidator
import com.wks.servicemarketplace.common.auth.TokenValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.stream.Collectors

@Configuration
class TokenValidatorConfiguration {

    @Bean
    fun tokenValidator(objectMapper: ObjectMapper): TokenValidator {
        return StandardTokenValidator(publicKey(), objectMapper)
    }

    private fun publicKey(): PublicKey {
        try {
            InputStreamReader(javaClass.classLoader.getResourceAsStream("publicKey.pem")!!).use { inputStreamReader ->
                BufferedReader(inputStreamReader).use { reader ->
                    val content = reader.lines().collect(Collectors.joining(""))
                            .replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                    val keyFactory = KeyFactory.getInstance("RSA")
                    val keySpec = X509EncodedKeySpec(Base64.getMimeDecoder().decode(content))
                    return keyFactory.generatePublic(keySpec)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Failed to load public key", e)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw RuntimeException("Failed to load public key", e)
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
            throw RuntimeException("Failed to load public key", e)
        }
    }
}