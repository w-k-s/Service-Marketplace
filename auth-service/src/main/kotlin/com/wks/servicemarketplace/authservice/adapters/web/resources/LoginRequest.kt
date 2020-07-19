package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wks.servicemarketplace.authservice.core.Credentials

data class LoginRequest @JsonCreator constructor(@JsonProperty("username") override val username: String,
                                                 @JsonProperty("password") override val password: String) : Credentials