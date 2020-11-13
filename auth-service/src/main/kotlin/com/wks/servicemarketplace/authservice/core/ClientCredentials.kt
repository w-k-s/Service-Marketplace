package com.wks.servicemarketplace.authservice.core

interface ClientCredentials {
    val clientId: String
    val clientSecret: String
    val requestedPermissions: List<String>
    val impersonationToken: String?
}