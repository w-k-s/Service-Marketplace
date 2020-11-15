package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.StandardTokenValidator
import com.wks.servicemarketplace.serviceproviderservice.adapters.auth.TokenValidator
import org.glassfish.hk2.api.Factory
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
import javax.inject.Inject

class TokenValidatorFactory @Inject constructor() : Factory<TokenValidator> {
    private val tokenValidator: StandardTokenValidator
    private val publicKey: PublicKey

    init {
        try {
            InputStreamReader(javaClass.classLoader.getResourceAsStream("publicKey.pem")).use { inputStreamReader ->
                BufferedReader(inputStreamReader).use { reader ->
                    val content = reader.lines().collect(Collectors.joining(""))
                            .replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                    val keyFactory = KeyFactory.getInstance("RSA")
                    val keySpec = X509EncodedKeySpec(Base64.getMimeDecoder().decode(content))
                    publicKey = keyFactory.generatePublic(keySpec)
                    tokenValidator = StandardTokenValidator(publicKey)
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

    override fun provide(): TokenValidator {
        return tokenValidator
    }

    override fun dispose(instance: TokenValidator) {}
}