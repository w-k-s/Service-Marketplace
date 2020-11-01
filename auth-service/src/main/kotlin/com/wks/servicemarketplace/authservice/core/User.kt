package com.wks.servicemarketplace.authservice.core

interface User {
    val username: String
    val role: String
    val permissions: List<String>
}