package com.wks.servicemarketplace.common.http

import com.wks.servicemarketplace.common.errors.ErrorType

fun ErrorType.httpStatusCode(): Int {
    return when (this) {
        ErrorType.AUTHENTICATION -> 401
        ErrorType.AUTHORIZATION -> 403
        ErrorType.VALIDATION,
        ErrorType.NOT_UNIQUE,
        ErrorType.INVALID_FORMAT -> 400
        ErrorType.RESOURCE_NOT_FOUND -> 404
        ErrorType.PROCESSING -> 428
        ErrorType.EXTERNAL_SYSTEM,
        ErrorType.UNKNOWN -> 500
    }
}

data class ErrorResponse(val type: ErrorType,
                         val message: String? = null,
                         val info: Map<String, String>? = null) {
    val code = type.code
}