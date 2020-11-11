package com.wks.servicemarketplace.authservice.core.dtos

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.authservice.core.ClientCredentials
import com.wks.servicemarketplace.authservice.core.utils.ModelValidator
import com.wks.servicemarketplace.authservice.core.Credentials
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonDeserialize(builder = ClientCredentialsRequest.Builder::class)
data class ClientCredentialsRequest constructor(override val clientId: String,
                                                override val clientSecret: String,
                                                override val requestedPermissions: List<String> = emptyList()) : ClientCredentials {

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    class Builder {
        @field:NotBlank
        var clientId: String? = null

        @field:NotBlank
        var clientSecret: String? = null

        @field:NotEmpty
        @field:NotNull
        var requestedPermission: List<String> = emptyList()

        fun clientId(clientId: String?): Builder {
            this.clientId = clientId
            return this
        }

        fun clientSecret(clientSecret: String?): Builder {
            this.clientSecret = clientSecret
            return this
        }

        fun requestedPermissions(permissions: List<String>): Builder {
            this.requestedPermission = requestedPermission
            return this
        }

        fun build(): ClientCredentialsRequest {
            ModelValidator.validate(this)
            return ClientCredentialsRequest(this.clientSecret!!, this.clientSecret!!, this.requestedPermission)
        }
    }
}
