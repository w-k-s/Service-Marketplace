package com.wks.servicemarketplace.authservice.core

interface IAMAdapter {
    fun login(credentials: Credentials): Token
    fun register(identity: Registration): Identity
}