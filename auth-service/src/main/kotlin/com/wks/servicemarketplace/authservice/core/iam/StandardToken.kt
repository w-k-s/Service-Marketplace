package com.wks.servicemarketplace.authservice.core.iam

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Token
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.NumericDate
import java.security.PrivateKey
import java.time.Duration
import java.time.Instant
import java.util.*

class StandardToken(subject: String,
                    user: User? = null,
                    permissions: List<String>,
                    expiration: Duration,
                    otherClaims: Map<String, String> = emptyMap(),
                    privateKey: PrivateKey
) : Token {

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
            user?.let { theUser -> claims.setClaim("user", theUser) }
            otherClaims.forEach { claim -> claims.setClaim(claim.key, claim.value) }
        }.toJson()
        it.key = privateKey
        it.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
    }.compactSerialization

    override val refreshToken: String? = null
}