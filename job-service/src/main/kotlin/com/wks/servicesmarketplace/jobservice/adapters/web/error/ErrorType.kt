package com.wks.servicesmarketplace.jobservice.adapters.web.error

import org.springframework.http.HttpStatus

enum class ErrorType(val code: Int, val httpStatus: HttpStatus) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION, HttpStatus.BAD_REQUEST),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND, HttpStatus.NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE, HttpStatus.BAD_REQUEST);

    companion object {
        private const val SERVICE_CODE = 1_00_000

        // -- Generic Errors
        private const val CATEGORY_CODE_GENERAL_ERROR = 0
        private const val GENERAL_ERROR_UNKNOWN: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 0                   // 100000
        private const val GENERAL_ERROR_VALIDATION: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 1                // 100001
        private const val GENERAL_ERROR_NOT_FOUND: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 10                // 100010
        private const val GENERAL_ERROR_INVALID_STATE: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 20            // 100020
    }
}