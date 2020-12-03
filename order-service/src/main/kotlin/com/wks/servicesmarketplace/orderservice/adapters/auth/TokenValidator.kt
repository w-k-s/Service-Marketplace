package com.wks.servicesmarketplace.orderservice.adapters.auth

import com.wks.servicesmarketplace.orderservice.core.auth.Authentication

interface TokenValidator {
    @Throws(InvalidTokenException::class)
    fun authenticate(token: String): Authentication
}