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
        override val message: String? = fields.toString(key = " "),
        override val errorType: ErrorType) : CoreException, RuntimeException(message)

data class RegistrationFailedException(override val fields: Map<String, List<String>> = emptyMap(),
                                       override val message: String? = fields.toString(key = " "),
                                       override val errorType: ErrorType) : CoreException, RuntimeException(message)

class ValidationException(fields: Map<String, List<String>>) : CoreException, RuntimeException(fields.toString(key = " ")) {
    override val errorType = ErrorType.VALIDATION
    override val fields: Map<String, List<String>> = Collections.unmodifiableMap(fields)
}

fun Map<String, List<String>>.toString(value: String = ",",
                                       key: String = "\n"): String {
    return this.map { it.value.joinToString(value) }.joinToString(key)
}