package com.wks.servicemarketplace.authservice.core

interface IAMAdapter {
    fun login(credentials: Credentials): User
    fun register(registration: Registration): User
    fun apiToken(clientCredentials: ClientCredentials): Client
}