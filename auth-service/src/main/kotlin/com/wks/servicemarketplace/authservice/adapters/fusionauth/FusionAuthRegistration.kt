package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Identity

data class FusionAuthRegistration(@JsonProperty("id") override val id: String) : Identity

