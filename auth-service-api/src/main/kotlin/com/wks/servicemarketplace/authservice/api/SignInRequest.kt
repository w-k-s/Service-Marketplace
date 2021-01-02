package com.wks.servicemarketplace.authservice.api

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicemarketplace.common.Password
import javax.validation.constraints.NotNull

data class SignInRequest internal constructor(override val username: Email,
                                              override val password: Password) : Credentials {

    class Builder {
        @field:NotNull
        var username: String? = null

        @field:NotNull
        var password: String? = null

        fun build(): SignInRequest {
            ModelValidator.validate(this)
            return SignInRequest(Email.of(this.username!!), Password.of(this.password!!))
        }
    }
}
