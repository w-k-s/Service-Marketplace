package com.wks.servicemarketplace.common.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.UserId
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.NumericDate
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.security.PrivateKey
import java.security.PublicKey
import java.time.*


class StandardToken(subject: String,
                    userId: UserId?,
                    permissions: List<String>,
                    expiration: Duration,
                    otherClaims: Map<String, String> = emptyMap(),
                    privateKey: PrivateKey
) : Token {

    override val expirationTimeUTC: OffsetDateTime
    override val accessToken: String

    init {
        val expiryMillis = Instant.now().plus(expiration)
        this.expirationTimeUTC = OffsetDateTime.ofInstant(expiryMillis, ZoneId.systemDefault())
                .withOffsetSameInstant(ZoneOffset.UTC)
        this.accessToken = JsonWebSignature().also {
            it.payload = JwtClaims().also { claims ->
                claims.setIssuedAtToNow()
                claims.expirationTime = NumericDate.fromMilliseconds(expiryMillis.toEpochMilli())
                claims.subject = subject
                claims.setStringListClaim("permissions", permissions)
                userId?.let { claims.setClaim("userId", it.toString()) }
                otherClaims.forEach { claim -> claims.setClaim(claim.key, claim.value) }
            }.toJson()
            it.key = privateKey
            it.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        }.compactSerialization
    }

    override val refreshToken: String? = null

    data class Claims(
            val subject: String,
            val user: UserId? = null,
            val permissions: List<String>
    )

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
                        UserId.fromString(it.getStringClaimValue("userId")),
                        it.getStringListClaimValue("permissions")
                )
            }
        }
    }
}