package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Identity

data class FusionAuthRegistrationResponse private constructor(@JsonProperty("user") val user: FusionAuthRegisteredUser) : Identity {
    data class FusionAuthRegisteredUser(@JsonProperty("id") val id: String)

    override val id: String
        get() = user.id
}

