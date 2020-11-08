package com.wks.servicemarketplace.authservice.core

interface Identity {
    val id: String
    val username: String
    val firstName: String
    val lastName: String
    val email: String
    val type: UserType
}