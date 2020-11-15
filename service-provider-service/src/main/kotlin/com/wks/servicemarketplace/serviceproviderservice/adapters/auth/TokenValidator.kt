package com.wks.servicemarketplace.serviceproviderservice.adapters.auth

import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication

interface TokenValidator {
    @Throws(InvalidTokenException::class)
    fun authenticate(token: String): Authentication
}