package com.wks.servicesmarketplace.jobservice.adapters.web

import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorType
import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorResponse
import com.wks.servicesmarketplace.jobservice.core.exceptions.AddressNotFoundException
import com.wks.servicesmarketplace.jobservice.core.exceptions.InvalidStateTransitionException
import com.wks.servicesmarketplace.jobservice.core.exceptions.ServiceOrderNotFoundException
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.modelling.command.AggregateNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [JSR303ViolationException::class])
    protected fun handleValidations(ex: Exception?, request: WebRequest?): ResponseEntity<Any?>? {
        val validationException = ex as JSR303ViolationException
        val errorResponse = ErrorResponse(
                ErrorType.VALIDATION,
                "Validation Failed",
                validationException.violations.map { it.propertyPath.toString() to it.message }.toMap()
        )
        return handleExceptionInternal(ex!!, errorResponse,
                HttpHeaders(), errorResponse.type.httpStatus, request!!)
    }

    @ExceptionHandler(value = [
        AggregateNotFoundException::class,
        ServiceOrderNotFoundException::class,
        AddressNotFoundException::class])
    protected fun handleAggregateNotFound(ex: Exception?, request: WebRequest?): ResponseEntity<Any?>? {
        val resourceId = when (ex) {
            is AggregateNotFoundException -> ex.aggregateIdentifier.toString()
            is ServiceOrderNotFoundException -> ex.orderId
            is AddressNotFoundException -> ex.addressExternalId.toString()
            else -> ""
        }
        val errorResponse = ErrorResponse(
                ErrorType.NOT_FOUND,
                "Resource not found",
                mapOf("id" to resourceId)
        )
        return handleExceptionInternal(ex!!, errorResponse,
                HttpHeaders(), errorResponse.type.httpStatus, request!!)
    }

    @ExceptionHandler(value = [InvalidStateTransitionException::class])
    protected fun handleInvalidStateTransition(ex: Exception?, request: WebRequest?): ResponseEntity<Any?>? {
        val aggregateException = ex as InvalidStateTransitionException
        val errorResponse = ErrorResponse(
                ErrorType.INVALID_STATE,
                aggregateException.message ?: "Invalid State Transition",
                mapOf(
                        "type" to (aggregateException.aggregate.simpleName ?: ""),
                        "from" to aggregateException.from,
                        "to" to aggregateException.to
                )
        )
        return handleExceptionInternal(ex!!, errorResponse,
                HttpHeaders(), errorResponse.type.httpStatus, request!!)
    }
}