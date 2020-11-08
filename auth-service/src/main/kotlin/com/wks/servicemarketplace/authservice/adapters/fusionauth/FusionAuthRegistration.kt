package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Identity
import com.wks.servicemarketplace.authservice.core.UserType

data class FusionAuthRegistration(@JsonProperty("id") override val id: String,
                                  @JsonProperty("username") override val username: String,
                                  @JsonProperty("firstName") override val firstName: String,
                                  @JsonProperty("lastName") override val lastName: String,
                                  @JsonProperty("email") override val email: String,
                                  @JsonProperty("userType") override val type: UserType) : Identity

