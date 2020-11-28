package com.wks.servicemarketplace.authservice.core.errors

import java.util.*

interface CoreException {
    val errorType: ErrorType
    val fields: Map<String, List<String>>
    val message: String?
}

data class UnauthorizedException(override val message: String) : CoreException, RuntimeException(message) {
    override val errorType = ErrorType.AUTHORIZATION
    override val fields: Map<String, List<String>> = emptyMap()
}

data class LoginFailedException(
        override val fields: Map<String, List<String>> = emptyMap(),
        override val message: String? = fields.toFormattedString(),
        override val errorType: ErrorType) : CoreException, RuntimeException(message)

data class RegistrationFailedException(override val fields: Map<String, List<String>> = emptyMap(),
                                       override val message: String? = fields.toFormattedString(),
                                       override val errorType: ErrorType) : CoreException, RuntimeException(message)

class ValidationException(fields: Map<String, List<String>>) : CoreException, RuntimeException(fields.toFormattedString()) {
    override val errorType = ErrorType.VALIDATION
    override val fields: Map<String, List<String>> = Collections.unmodifiableMap(fields)
}

internal fun Map<String, List<String>>.toFormattedString(valueSeparator: String = ",",
                                                         keySeparator: String = " "): String {
    return this.map { "${it.key}: ${it.value.joinToString { valueSeparator }}" }.joinToString(keySeparator)
}