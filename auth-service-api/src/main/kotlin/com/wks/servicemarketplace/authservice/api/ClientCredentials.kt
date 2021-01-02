package com.wks.servicemarketplace.authservice.api

interface ClientCredentials {
    val clientId: String
    val clientSecret: String
    val requestedPermissions: List<String>
    val impersonationToken: String?
}