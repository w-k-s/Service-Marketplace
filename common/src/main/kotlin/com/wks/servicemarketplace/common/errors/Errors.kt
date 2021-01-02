package com.wks.servicemarketplace.common.errors

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),

    LOGIN_FAILED(ErrorType.AUTHENTICATION_ERROR_UNKNOWN),
    INVALID_TOKEN(ErrorType.AUTHENTICATION_ERROR_INVALID_TOKEN),
    USER_NOT_FOUND(ErrorType.AUTHENTICATION_ERROR_USER_NOT_FOUND),
    AUTHORIZATION(ErrorType.AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS),

    REGISTRATION_FAILED(ErrorType.REGISTRATION_ERROR_UNKNOWN),
    DUPLICATE_USERNAME(ErrorType.REGISTRATION_ERROR_DUPLICATE_USERNAME),
    REGISTRATION_IN_PROGRESS(ErrorType.REGISTRATION_ERROR_IN_PROGRESS);

    /**
     * S: Service Code i.e. Microservice code (n-digits)
     * CC: Category Code i.e. Error Group code (n-digits)
     * EEE: Error Code i.e. Error Code (3 digits)
     */
    companion object {

        // General Errors
        private const val GENERAL_ERROR_UNKNOWN: Int = 1_000
        private const val GENERAL_ERROR_VALIDATION: Int = 1_001

        // Authentication Errors
        private const val AUTHENTICATION_ERROR_UNKNOWN = 2_000
        private const val AUTHENTICATION_ERROR_INVALID_TOKEN = 2_001
        private const val AUTHENTICATION_ERROR_USER_NOT_FOUND = 2_002
        private const val AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS = 2_003

        // Registration Errors
        private const val REGISTRATION_ERROR_UNKNOWN = 3_000
        private const val REGISTRATION_ERROR_DUPLICATE_USERNAME = 3_001
        private const val REGISTRATION_ERROR_IN_PROGRESS = 3_010
    }

}

interface HasErrorDetails {
    val errorDetails: Map<String, List<String>>
}

open class CoreException(val errorType: ErrorType,
                         override val message: String? = null,
                         override val cause: Throwable? = null
) : Exception(message, cause)

open class CoreRuntimeException(val errorType: ErrorType,
                                override val message: String? = null,
                                override val cause: Throwable? = null
) : RuntimeException(message, cause)

class ValidationException(override val errorDetails: Map<String, List<String>>,
                          message: String? = errorDetails.toFormattedString())
    : CoreException(ErrorType.VALIDATION, message), HasErrorDetails

class UnauthorizedException(message: String? = "Insufficient Privileges") : CoreRuntimeException(ErrorType.AUTHORIZATION, message)

class InvalidTokenException(message: String? = null,
                            cause: Throwable? = null) : CoreRuntimeException(ErrorType.INVALID_TOKEN, message, cause)

internal fun Map<String, List<String>>.toFormattedString(valueSeparator: String = ",",
                                                         keySeparator: String = " "): String {
    return this.map { "${it.key}: ${it.value.joinToString { valueSeparator }}" }.joinToString(keySeparator)
}