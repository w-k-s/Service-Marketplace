package com.wks.servicemarketplace.authservice.core.iam

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.core.Token
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.NumericDate
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import java.time.Instant


class StandardToken(subject: String,
                    user: User? = null,
                    permissions: List<String>,
                    expiration: Duration,
                    otherClaims: Map<String, String> = emptyMap(),
                    privateKey: PrivateKey
) : Token {

    data class Claims(
            val subject: String,
            val user: User? = null,
            val permissions: List<String>
    )

    data class User(
            val id: String,
            val firstName: String,
            val lastName: String,
            val username: String,
            val email: String,
            val role: String
    )

    @JsonProperty("accessToken")
    override val accessToken: String = JsonWebSignature().also {
        it.payload = JwtClaims().also { claims ->
            claims.setIssuedAtToNow()
            claims.expirationTime = NumericDate.fromMilliseconds(Instant.now().plusMillis(expiration.toMillis()).toEpochMilli())
            claims.subject = subject
            claims.setStringListClaim("permissions", permissions)
            user?.let { theUser -> claims.setStringClaim("user", objectMapper.writeValueAsString(theUser)) }
            otherClaims.forEach { claim -> claims.setClaim(claim.key, claim.value) }
        }.toJson()
        it.key = privateKey
        it.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
    }.compactSerialization

    override val refreshToken: String? = null

    companion object {
        val objectMapper = ObjectMapper()
        fun parseClaims(token: String, publicKey: PublicKey): Claims {
            val jwtConsumer = JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setRequireSubject()
                    .setVerificationKey(publicKey)
                    .setJwsAlgorithmConstraints(
                            AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                    .build()

            val jwtClaims = jwtConsumer.processToClaims(token)
            return jwtClaims.let {
                Claims(
                        it.subject,
                        it.getClaimValue("user", User::class.java),
                        it.getStringListClaimValue("permissions")
                )
            }
        }
    }
}