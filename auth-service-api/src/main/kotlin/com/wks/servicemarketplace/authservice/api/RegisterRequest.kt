package com.wks.servicemarketplace.authservice.api

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.auth.UserType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RegisterRequest internal constructor(override val name: Name,
                                                override val email: Email,
                                                override val mobileNumber: PhoneNumber,
                                                override val password: Password,
                                                override val userType: UserType) : Registration {

    override val username: String
        get() = email.value

    override val enabled: Boolean
        get() = false

    class Builder {
        @field:NotBlank
        @field:Size(min = 2, max = 50)
        var firstName: String? = null

        @field:Size(min = 2, max = 50)
        var lastName: String? = null

        @field:NotNull
        var email: String? = null

        @field:NotNull
        var mobileNumber: String? = null

        var password: String? = null

        @field:NotNull
        var userType: UserType? = null

        fun build(): RegisterRequest {
            ModelValidator.validate(this)
            return RegisterRequest(
                    Name.of(firstName!!, lastName!!),
                    Email.of(email!!),
                    PhoneNumber.of(mobileNumber!!),
                    Password.of(password!!),
                    userType!!
            )
        }
    }
}
