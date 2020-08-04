package com.wks.servicesmarketplace.jobservice.core.auth

import java.lang.RuntimeException

class Authorization {
    companion object {
        fun hasRole(user: User?, role: String) {
            user?.roles?.map { it.authority }?.contains(role)
                    ?: throw AuthorizationException("User does not have role: $role")
        }
    }
}

class AuthorizationException(message: String) : RuntimeException(message)

