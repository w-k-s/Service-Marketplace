package com.wks.servicesmarketplace.jobservice.adapters.web.error

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = [ "code", "type", "description", "fields" ])
data class ErrorResponse(val type: ErrorType,
                         val description: String,
                         val fields: Map<String,String>){
    val code : Int = type.code
}