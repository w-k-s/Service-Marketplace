package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty

data class FusionAuthRegistrationRequest internal constructor(@JsonProperty("registration") val registration: Registration,
                                                              @JsonProperty("user") val user: User) {

    data class Registration(@JsonProperty("username") val username: String,
                            @JsonProperty("applicationId") val applicationId: String) {
        constructor(registration: com.wks.servicemarketplace.authservice.core.Registration, applicationId: String)
                : this(registration.username, applicationId)
    }

    data class User(@JsonProperty("username") val username: String,
                    @JsonProperty("password") val password: String,
                    @JsonProperty("email") val email: String,
                    @JsonProperty("firstName") val firstName: String,
                    @JsonProperty("lastName") val lastName: String) {
        constructor(registration: com.wks.servicemarketplace.authservice.core.Registration) : this(
                registration.username,
                registration.password,
                registration.email,
                registration.firstName,
                registration.lastName
        )
    }

    constructor(registration: com.wks.servicemarketplace.authservice.core.Registration, applicationId: String)
            : this(Registration(registration, applicationId), User(registration))
}