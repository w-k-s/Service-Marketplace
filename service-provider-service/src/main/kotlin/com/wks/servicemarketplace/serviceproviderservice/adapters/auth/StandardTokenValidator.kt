package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jwt.MalformedClaimException
import org.jose4j.jwt.consumer.ErrorCodeValidator
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import java.security.PublicKey

class StandardTokenValidator(publicKey: PublicKey) : TokenValidator {
    private val LOGGER = LoggerFactory.getLogger(StandardTokenValidator::class.java)

    private var consumer = JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setAllowedClockSkewInSeconds(30)
            .setRequireSubject()
            .setVerificationKey(publicKey)
            .setJwsAlgorithmConstraints(
                    AlgorithmConstraints.ConstraintType.PERMIT,
                    AlgorithmIdentifiers.RSA_USING_SHA256
            )
            .build()


    @Throws(InvalidTokenException::class)
    override fun authenticate(token: String): Authentication {
        try {
            return consumer.processToClaims(token).let {
                DefaultAuthentication(
                        it.getClaimValue("user", DefaultUser::class.java),
                        token,
                        it.subject,
                        it.getStringListClaimValue("permissions")
                )
            }

        } catch (e: InvalidJwtException) {
            LOGGER.error("Invalid JWT Token: {}", e.message, e)
            throw InvalidTokenException(e.errorDetails
                    .map(ErrorCodeValidator.Error::getErrorMessage)
                    .joinToString { "," },
                    e
            )
        } catch (e: MalformedClaimException) {
            LOGGER.error("Invalid Claim: {}", e.message, e)
            throw InvalidTokenException("Token's claims could not be read", e)
        }
    }
}