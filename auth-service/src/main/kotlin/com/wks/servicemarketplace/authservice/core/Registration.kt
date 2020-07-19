package com.wks.servicemarketplace.authservice.core

interface Registration {
    val username: String
    val password: String
    val firstName: String
    val lastName: String
    val email: String
    val enabled: Boolean
    val userType: UserType
};