package com.wks.servicesmarketplace.orderservice.adapters.web.error

import com.fasterxml.jackson.annotation.JsonInclude
import com.wks.servicemarketplace.common.errors.ErrorType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(val type: ErrorType,
                         val description: String? = null,
                         val fields: Map<String, String> = emptyMap()) {
    val code: Int = type.code
}