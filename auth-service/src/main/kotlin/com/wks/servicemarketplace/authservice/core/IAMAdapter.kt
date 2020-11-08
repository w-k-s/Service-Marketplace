package com.wks.servicemarketplace.authservice.core

interface IAMAdapter {
    fun login(credentials: Credentials): User
    fun register(registration: Registration): Identity
}