package com.wks.servicemarketplace.authservice.core.dtos

import com.wks.servicemarketplace.authservice.core.utils.ModelValidator
import com.wks.servicemarketplace.authservice.core.Credentials
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class SignInRequest internal constructor(override val username: String,
                                     override val password: String) : Credentials {

    class Builder {
        @field:NotBlank
        @field:Email
        var username: String? = null

        @field:NotBlank
        @field:Password
        var password: String? = null

        fun username(username: String?): Builder {
            this.username = username
            return this
        }

        fun password(password: String?): Builder {
            this.password = password
            return this
        }

        fun build(): SignInRequest {
            ModelValidator.validate(this)
            return SignInRequest(this.username!!, this.password!!)
        }
    }
}
