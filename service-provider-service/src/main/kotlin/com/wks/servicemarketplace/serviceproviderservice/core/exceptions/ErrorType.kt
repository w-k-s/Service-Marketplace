package com.wks.servicemarketplace.serviceproviderservice.core.exceptions

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE),

    AUTHORIZATION(ErrorType.AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS);

    /**
     * Error Code Format: S_CC_EEE
     *
     * S: Service Code i.e. Microservice code (n-digits)
     * CC: Category Code i.e. Error Group code (2-digits)
     * EEE: Error Code i.e. Error Code (3 digits)
     */
    companion object {

        // General Errors
        private const val GENERAL_ERROR_UNKNOWN: Int = 4_00_000
        private const val GENERAL_ERROR_VALIDATION: Int = 4_00_001
        private const val GENERAL_ERROR_NOT_FOUND: Int = 4_00_010
        private const val GENERAL_ERROR_INVALID_STATE: Int = 4_00_020

        // Authorization Errors
        private const val AUTHORIZATION_ERROR_INCORRECT_PERMISSIONS: Int = 4_01_000

    }
}