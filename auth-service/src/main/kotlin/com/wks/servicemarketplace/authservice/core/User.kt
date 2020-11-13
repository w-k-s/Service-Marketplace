package com.wks.servicemarketplace.authservice.core

interface User {
    val id: String
    val firstName: String
    val lastName: String
    val username: String
    val email: String
    val role: String
    val type: UserType
    val permissions: List<String>
}