package com.wks.servicemarketplace.authservice.core.errors

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE),

    AUTHENTICATION(ErrorType.AUTHENTICATION_ERROR_INCORRECT_CREDENTIALS),
    AUTHORIZATION(ErrorType.AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS),

    DUPLICATE_USERNAME(ErrorType.REGISTRATION_ERROR_DUPLICATE_USERNAME);

    /**
     * Error Code Format: S_CC_EEE
     *
     * S: Service Code i.e. Microservice code (n-digits)
     * CC: Category Code i.e. Error Group code (2-digits)
     * EEE: Error Code i.e. Error Code (3 digits)
     */
    companion object {

        // General Errors
        private const val GENERAL_ERROR_UNKNOWN: Int = 3_00_000
        private const val GENERAL_ERROR_VALIDATION: Int = 3_00_001
        private const val GENERAL_ERROR_NOT_FOUND: Int = 3_00_010
        private const val GENERAL_ERROR_INVALID_STATE: Int = 3_00_020

        // Authentication Errors
        private const val AUTHENTICATION_ERROR_INCORRECT_CREDENTIALS: Int = 3_01_000

        // Authorization Errors
        private const val AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS: Int = 3_02_000

        // Registration Errors
        private const val REGISTRATION_ERROR_DUPLICATE_USERNAME: Int = 3_03_000
    }
}