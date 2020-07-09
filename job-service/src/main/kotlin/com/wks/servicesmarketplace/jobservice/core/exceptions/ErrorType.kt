package com.wks.servicesmarketplace.jobservice.core.exceptions

enum class ErrorType(val code: Int) {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE),
    ADDRESS_NOT_FOUND(ErrorType.SERVICE_ORDER_ERROR_ADDRESS_NOT_FOUND),
    SERVICE_ORDER_NOT_FOUND(ErrorType.SERVICE_ORDER_ERROR_ORDER_NOT_FOUND);

    companion object {
        private const val SERVICE_CODE = 1_00_000

        // -- Generic Errors
        private const val CATEGORY_CODE_GENERAL_ERROR = 0
        private const val GENERAL_ERROR_UNKNOWN: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 0                   // 100000
        private const val GENERAL_ERROR_VALIDATION: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 1                // 100001
        private const val GENERAL_ERROR_NOT_FOUND: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 10                // 100010
        private const val GENERAL_ERROR_INVALID_STATE: Int = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 20            // 100020

        // -- Service Order Errors
        private const val CATEGORY_CODE_SERVICE_ORDER_ERROR = 100
        private const val SERVICE_ORDER_ERROR_ADDRESS_NOT_FOUND = SERVICE_CODE + CATEGORY_CODE_SERVICE_ORDER_ERROR + 10;// 100110
        private const val SERVICE_ORDER_ERROR_ORDER_NOT_FOUND = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 20         // 100120
    }
}