package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.core.Registration
import com.wks.servicemarketplace.authservice.core.UserType

data class RegistrationRequest(override val firstName: String,
                               override val lastName: String,
                               override val email: String,
                               override val password: String,
                               override val userType: UserType) : Registration {

    override val username: String
        get() = email

    override val enabled: Boolean
        get() = false
}