package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.authservice.api.ClientCredentials
import com.wks.servicemarketplace.authservice.api.Credentials
import com.wks.servicemarketplace.authservice.api.Registration
import com.wks.servicemarketplace.common.auth.User
import com.wks.servicemarketplace.common.auth.UserRole

interface IAMAdapter {
    fun login(credentials: Credentials): User
    fun register(registration: Registration): User
    fun apiToken(clientCredentials: ClientCredentials): Client
    fun assignRole(role: UserRole, userId: String)
}