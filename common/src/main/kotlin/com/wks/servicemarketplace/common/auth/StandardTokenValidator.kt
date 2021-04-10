package com.wks.servicemarketplace.common.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Result
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jwt.MalformedClaimException
import org.jose4j.jwt.consumer.ErrorCodeValidator
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import java.security.PublicKey

class StandardTokenValidator(publicKey: PublicKey, private val objectMapper: ObjectMapper) : TokenValidator {

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


    override fun authenticate(token: String): Authentication {
        try {
            return consumer.processToClaims(token).let {
                DefaultAuthentication(
                        it.getStringClaimValue("userId")?.let { uuidString -> UserId.fromString(uuidString) },
                        it.subject,
                        it.getStringListClaimValue("permissions")
                )
            }

        } catch (e: InvalidJwtException) {
            LOGGER.error("Invalid JWT Token: {}", e.message, e)
            throw CoreException(ErrorType.AUTHENTICATION,
                    e.errorDetails.map(ErrorCodeValidator.Error::getErrorMessage).joinToString { "," },
                    e
            )
        } catch (e: MalformedClaimException) {
            LOGGER.error("Invalid Claim: {}", e.message, e)
            throw CoreException(ErrorType.INVALID_FORMAT, "Token's claims could not be read", e)
        }
    }
}