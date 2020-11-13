package com.wks.servicemarketplace.authservice.core.dtos

import com.wks.servicemarketplace.authservice.core.utils.ModelValidator
import com.wks.servicemarketplace.authservice.core.Registration
import com.wks.servicemarketplace.authservice.core.UserType
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RegisterRequest internal constructor(override val firstName: String,
                                       override val lastName: String,
                                       override val email: String,
                                       override val password: String,
                                       override val userType: UserType) : Registration {

    override val username: String
        get() = email

    override val enabled: Boolean
        get() = false

    class Builder {
        @field:NotBlank
        @field:Size(min = 2, max = 50)
        var firstName: String? = null

        @field:Size(min = 2, max = 50)
        var lastName: String? = null
        @field:Email
        @field:NotBlank
        var email: String? = null
        @field:Password
        var password: String? = null
        @field:NotNull
        var userType: UserType? = null

        fun firstName(firstName: String?): Builder {
            this.firstName = firstName
            return this
        }

        fun lastName(lastName: String?): Builder {
            this.lastName = lastName
            return this
        }

        fun email(email: String?): Builder {
            this.email = email
            return this
        }

        fun password(password: String?): Builder {
            this.password = password
            return this
        }

        fun userType(userType: UserType?): Builder {
            this.userType = userType
            return this
        }

        fun build(): RegisterRequest {
            ModelValidator.validate(this)
            return RegisterRequest(
                    firstName!!,
                    lastName!!,
                    email!!,
                    password!!,
                    userType!!
            )
        }
    }
}
