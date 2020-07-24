package com.wks.servicemarketplace.authservice.config

data class KeycloakConfiguration(
        val serverUrl: String,
        val realm: String,
        val adminId: String,
        val adminSecret: String,
        val clientId: String,
        val clientSecret: String
)