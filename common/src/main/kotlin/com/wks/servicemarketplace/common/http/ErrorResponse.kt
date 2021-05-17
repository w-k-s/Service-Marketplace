package com.wks.servicemarketplace.common.http

import com.wks.servicemarketplace.common.errors.ErrorType

data class ErrorResponse(val type: ErrorType,
                         val message: String? = null,
                         val info: Map<String, String>? = null) {
    val code = type.code
}