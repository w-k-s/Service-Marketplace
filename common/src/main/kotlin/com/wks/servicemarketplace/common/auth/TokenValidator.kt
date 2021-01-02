package com.wks.servicemarketplace.common.auth

import com.wks.servicemarketplace.common.errors.InvalidTokenException


interface TokenValidator {
    @Throws(InvalidTokenException::class)
    fun authenticate(token: String): Authentication
}