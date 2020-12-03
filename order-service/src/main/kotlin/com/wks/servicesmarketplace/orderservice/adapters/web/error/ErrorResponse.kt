package com.wks.servicesmarketplace.orderservice.adapters.web.error

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.wks.servicesmarketplace.orderservice.core.exceptions.ErrorType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(val type: ErrorType,
                         val description: String? = null,
                         val fields: Map<String, List<String>> = emptyMap()) {
    val code: Int = type.code
}