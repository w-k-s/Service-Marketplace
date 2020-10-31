package com.wks.servicemarketplace.authservice.core.errors

import java.util.*

interface CoreException {
    val errorType: ErrorType
    val fields: Map<String, String>?
    val message: String?
}

data class UserNotFoundException(val username: String) : CoreException, RuntimeException("'$username' is not a registered user") {
    override val errorType = ErrorType.NOT_FOUND
    override val fields = mapOf("username" to this.username)
}

data class DuplicateUsernameException(val username: String) : CoreException, RuntimeException("'$username' is already registered") {
    override val errorType = ErrorType.DUPLICATE_USERNAME
    override val fields = mapOf("username" to this.username)
}

class UnauthorizedException : CoreException, RuntimeException("Unauthorized") {
    override val errorType = ErrorType.AUTHENTICATION
    override val fields = emptyMap<String, String>()
}

class ValidationException(fields: Map<String, String>) : CoreException, RuntimeException(buildErrorMessage(fields)) {
    override val errorType = ErrorType.VALIDATION
    override val fields: Map<String, String> = Collections.unmodifiableMap(fields)

    companion object {
        private fun buildErrorMessage(fields: Map<String, String>): String {
            return fields.entries
                    .map { "${it.key}: ${it.value}" }
                    .joinToString(",")
        }
    }
}