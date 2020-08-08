package com.wks.servicemarketplace.authservice.adapters.errors

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE);

    companion object {
        private const val SERVICE_CODE = 3_00_000

        // -- Generic Errors
        private const val CATEGORY_CODE_GENERAL_ERROR = 0
        private const val GENERAL_ERROR_UNKNOWN: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 0                   // 300000
        private const val GENERAL_ERROR_VALIDATION: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 1                // 300001
        private const val GENERAL_ERROR_NOT_FOUND: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 10                // 300010
        private const val GENERAL_ERROR_INVALID_STATE: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 20            // 300020
    }
}