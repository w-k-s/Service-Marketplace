package com.wks.servicemarketplace.authservice.core.errors

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),

    LOGIN_FAILED(ErrorType.AUTHENTICATION_ERROR_UNKNOWN),
    USER_NOT_FOUND(ErrorType.AUTHENTICATION_ERROR_USER_NOT_FOUND),
    AUTHORIZATION(ErrorType.AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS),

    REGISTRATION_FAILED(ErrorType.REGISTRATION_ERROR_UNKNOWN),
    DUPLICATE_USERNAME(ErrorType.REGISTRATION_ERROR_DUPLICATE_USERNAME),
    REGISTRATION_IN_PROGRESS(ErrorType.REGISTRATION_ERROR_IN_PROGRESS);

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

        // Authentication Errors
        private const val AUTHENTICATION_ERROR_UNKNOWN = 3_00_000
        private const val AUTHENTICATION_ERROR_USER_NOT_FOUND = 3_01_001
        private const val AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS = 3_01_002

        // Registration Errors
        private const val REGISTRATION_ERROR_UNKNOWN = 3_02_000
        private const val REGISTRATION_ERROR_DUPLICATE_USERNAME = 3_02_001
        private const val REGISTRATION_ERROR_IN_PROGRESS = 3_02_010
    }
}