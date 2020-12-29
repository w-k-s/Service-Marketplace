package com.wks.servicemarketplace.authservice.core.iam

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.core.Email
import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.UserId
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
            val id: UserId,
            val firstName: String,
            val lastName: String,
            val username: Email,
            val email: Email,
            val role: String
    )

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
                user?.let { theUser -> claims.setClaim("user", objectMapper.convertValue(theUser, Map::class.java)) }
                otherClaims.forEach { claim -> claims.setClaim(claim.key, claim.value) }
            }.toJson()
            it.key = privateKey
            it.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        }.compactSerialization
    }

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
                        objectMapper.convertValue(it.getClaimValue("user", Map::class.java), User::class.java),
                        it.getStringListClaimValue("permissions")
                )
            }
        }
    }
}