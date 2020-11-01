package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.core.*
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.NumericDate
import java.security.PrivateKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TokenService @Inject constructor(private val iam: IAMAdapter,
                                       private val privateKey: PrivateKey) {

    fun login(credentials: Credentials): Token {
        val user = iam.login(credentials)

        // Create IAM Independent, standard key
        val claims = JwtClaims()
        claims.setIssuedAtToNow()
        claims.expirationTime = NumericDate.fromMilliseconds(Instant.now().plus(1L, ChronoUnit.HOURS).toEpochMilli())
        claims.subject = user.username
        claims.setClaim("role", user.role)
        claims.setStringListClaim("permissions", user.permissions)

        val jws = JsonWebSignature()
        jws.payload = claims.toJson()
        jws.key = privateKey

        jws.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        return StandardToken(jws.compactSerialization)
    }

    fun register(registration: Registration): Identity {
        return iam.register(registration)
    }
}