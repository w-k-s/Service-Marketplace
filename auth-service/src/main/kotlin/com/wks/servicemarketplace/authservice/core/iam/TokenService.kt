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

        val jws = JsonWebSignature().also {
            it.payload = JwtClaims().also {
                it.setIssuedAtToNow()
                it.setIssuedAtToNow()
                it.expirationTime = NumericDate.fromMilliseconds(Instant.now().plus(1L, ChronoUnit.HOURS).toEpochMilli())
                it.subject = user.username
                it.setClaim("role", user.role)
                it.setStringListClaim("permissions", user.permissions)
            }.toJson()
            it.key = privateKey
            it.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        }

        return StandardToken(jws.compactSerialization)
    }

    fun register(registration: Registration) = iam.register(registration)
}