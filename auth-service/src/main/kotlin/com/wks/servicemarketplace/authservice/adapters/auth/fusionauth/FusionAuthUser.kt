package com.wks.servicemarketplace.authservice.adapters.auth.fusionauth

import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.UserId
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserRole
import com.wks.servicemarketplace.common.auth.UserType

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