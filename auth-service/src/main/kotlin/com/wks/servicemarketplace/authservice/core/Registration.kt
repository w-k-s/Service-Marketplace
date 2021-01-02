package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.Password
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.auth.UserType

interface Registration {
    val username: String
    val password: Password
    val name: Name
    val email: Email
    val mobileNumber: PhoneNumber
    val enabled: Boolean
    val userType: UserType
};