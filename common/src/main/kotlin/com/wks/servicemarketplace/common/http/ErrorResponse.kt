package com.wks.servicemarketplace.common.http

import com.wks.servicemarketplace.common.errors.ErrorType

fun ErrorType.httpStatusCode(): Int {
    return when (this) {
        ErrorType.INVALID_TOKEN,
        ErrorType.AUTHORIZATION -> 403
        ErrorType.VALIDATION,
        ErrorType.DUPLICATE_USERNAME -> 400
        ErrorType.USER_NOT_FOUND -> 404
        ErrorType.REGISTRATION_IN_PROGRESS -> 422
        ErrorType.UNKNOWN,
        ErrorType.LOGIN_FAILED,
        ErrorType.REGISTRATION_FAILED -> 500
    }
}

data class ErrorResponse(private val type: ErrorType,
                         private val message: String? = null,
                         private val info: Map<String, List<String>>? = null) {
    private val code = type.code
}