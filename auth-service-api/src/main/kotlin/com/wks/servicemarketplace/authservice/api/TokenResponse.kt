package com.wks.servicemarketplace.authservice.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.common.auth.Token
import java.time.OffsetDateTime


data class TokenResponse @JsonCreator constructor(
        @JsonProperty("accessToken") override val accessToken: String,
        @JsonProperty("refreshToken")override val refreshToken: String?,
        @JsonProperty("expirationTimeUTC") override val expirationTimeUTC: OffsetDateTime
) : Token