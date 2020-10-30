package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Token

data class FusionAuthLoginResponse(@JsonProperty("token") override val accessToken: String,
                                   override val refreshToken: String? = null) : Token