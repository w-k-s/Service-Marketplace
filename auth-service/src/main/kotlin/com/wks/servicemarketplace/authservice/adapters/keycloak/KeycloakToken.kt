package com.wks.servicemarketplace.authservice.adapters.keycloak

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Token

data class KeycloakToken(@JsonProperty("access_token") override val accessToken: String,
                         @JsonProperty("refresh_token") override val refreshToken: String?) : Token {

}