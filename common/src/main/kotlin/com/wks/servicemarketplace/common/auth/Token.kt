package com.wks.servicemarketplace.common.auth

import java.time.Clock
import java.time.OffsetDateTime

interface Token {
    val accessToken: String
    val refreshToken: String?
    val expirationTimeUTC: OffsetDateTime
}

fun Token.isExpired() = this.expirationTimeUTC.isAfter(OffsetDateTime.now(Clock.systemUTC()))