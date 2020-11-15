package com.wks.servicemarketplace.serviceproviderservice.core.auth

import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.UnauthorizedException
import java.security.Principal
import kotlin.jvm.Throws

interface Authentication : Principal {
    val user: User?
    fun hasRole(role: String): Boolean
    @Throws(UnauthorizedException::class)
    fun checkRole(role: String)
}

interface User : Principal {
    val id: String?
}