package com.wks.servicemarketplace.authservice.core

interface User {
    val id: String
    val firstName: String
    val lastName: String
    val username: String
    val email: String
    val mobileNumber: String
    val role: UserRole
    val type: UserType
    val permissions: List<String>
}