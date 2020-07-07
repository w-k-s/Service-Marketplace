package com.wks.servicesmarketplace.jobservice.adapters.graphql

import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorType
import org.axonframework.messaging.interceptors.JSR303ViolationException


fun Throwable.toGraphQLException(): GraphQLException {
    return when (this) {
        is JSR303ViolationException -> {
            val fields = this.violations.map { it.propertyPath.toString() to it.message }.toMap()
            val message = fields.map { "${it.key}: ${it.value}" }.joinToString(";")
            GraphQLException(message, fields, ErrorType.VALIDATION)
        }
        else -> GraphQLException(this.message, ErrorType.UNKNOWN)
    }
}