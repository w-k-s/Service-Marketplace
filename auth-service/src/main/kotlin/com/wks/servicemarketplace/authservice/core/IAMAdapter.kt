package com.wks.servicemarketplace.authservice.core

import java.security.PublicKey

interface IAMAdapter {
    fun login(credentials: Credentials): User
    fun register(registration: Registration): Identity
}