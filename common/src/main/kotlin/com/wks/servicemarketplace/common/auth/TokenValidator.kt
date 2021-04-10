package com.wks.servicemarketplace.common.auth

interface TokenValidator {
    fun authenticate(token: String): Authentication
}