package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.wks.servicemarketplace.authservice.core.User
import com.wks.servicemarketplace.authservice.core.UserRole
import com.wks.servicemarketplace.authservice.core.UserType

data class FusionAuthUser(
        override val id: String,
        override val firstName: String,
        override val lastName: String,
        override val username: String,
        override val email: String,
        override val mobileNumber: String,
        override val role: UserRole,
        override val type: UserType,
        override val permissions: List<String>
) : User