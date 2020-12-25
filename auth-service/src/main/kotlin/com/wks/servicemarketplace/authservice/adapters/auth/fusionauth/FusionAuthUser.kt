package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.wks.servicemarketplace.authservice.core.*

data class FusionAuthUser(
        override val id: UserId,
        override val name: Name,
        override val username: Email,
        override val email: Email,
        override val mobileNumber: PhoneNumber,
        override val role: UserRole,
        override val type: UserType,
        override val permissions: List<String>
) : User