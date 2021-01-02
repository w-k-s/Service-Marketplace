package com.wks.servicemarketplace.authservice.api

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.common.ModelValidator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonDeserialize(builder = ClientCredentialsRequest.Builder::class)
data class ClientCredentialsRequest constructor(override val clientId: String,
                                                         override val clientSecret: String,
                                                         override val requestedPermissions: List<String> = emptyList(),
                                                         override val impersonationToken: String? = null) : ClientCredentials {

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    class Builder {
        @field:NotBlank
        var clientId: String? = null

        @field:NotBlank
        var clientSecret: String? = null

        @field:NotEmpty
        @field:NotNull
        var requestedPermission: List<String> = emptyList()

        var impersonationToken: String? = null

        fun build(): ClientCredentialsRequest {
            ModelValidator.validate(this)
            return ClientCredentialsRequest(
                    this.clientSecret!!,
                    this.clientSecret!!,
                    this.requestedPermission,
                    this.impersonationToken
            )
        }
    }
}
