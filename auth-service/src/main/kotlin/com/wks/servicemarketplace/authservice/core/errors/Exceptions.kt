package com.wks.servicemarketplace.authservice.core.errors

open class CoreException(val errorType: ErrorType,
                         override val message: String? = null,
                         val fields: Map<String, List<String>> = emptyMap(),
                         override val cause: Throwable? = null
) : Exception(message, cause)

data class UnauthorizedException(override val message: String) : CoreException(ErrorType.AUTHORIZATION, message)

class UserNotFoundException
    : CoreException(ErrorType.USER_NOT_FOUND, "User not found")

class LoginFailedException(message: String)
    : CoreException(ErrorType.LOGIN_FAILED, message)

class RegistrationFailedException(message: String)
    : CoreException(ErrorType.REGISTRATION_FAILED, message)

class RegistrationInProgressException(message: String)
    : CoreException(ErrorType.REGISTRATION_IN_PROGRESS, message)

class ValidationException(fields: Map<String, List<String>>)
    : CoreException(ErrorType.VALIDATION, fields.toFormattedString(), fields)

internal fun Map<String, List<String>>.toFormattedString(valueSeparator: String = ",",
                                                         keySeparator: String = " "): String {
    return this.map { "${it.key}: ${it.value.joinToString { valueSeparator }}" }.joinToString(keySeparator)
}