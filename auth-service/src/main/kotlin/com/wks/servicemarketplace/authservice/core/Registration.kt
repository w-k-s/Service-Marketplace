package com.wks.servicemarketplace.authservice.core

interface Registration {
    val username: String
    val password: Password
    val name: Name
    val email: Email
    val mobileNumber: PhoneNumber
    val enabled: Boolean
    val userType: UserType
};