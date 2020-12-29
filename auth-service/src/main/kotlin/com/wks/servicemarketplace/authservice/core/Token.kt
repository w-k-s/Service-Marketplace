package com.wks.servicemarketplace.authservice.core

import java.time.Clock
import java.time.OffsetDateTime

interface Token {
    val accessToken: String
    val refreshToken: String?
    val expirationTimeUTC: OffsetDateTime
}

fun Token.isExpired() = this.expirationTimeUTC.isAfter(OffsetDateTime.now(Clock.systemUTC()))