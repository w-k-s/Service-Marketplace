package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Credentials

data class FusionAuthLoginRequest(@JsonProperty("loginId") val loginId: String,
                                  @JsonProperty("password") val password: String,
                                  @JsonProperty("applicationId") val applicationId: String) {
    constructor(credentials: Credentials, applicationId: String) : this(credentials.username, credentials.password, applicationId)
}