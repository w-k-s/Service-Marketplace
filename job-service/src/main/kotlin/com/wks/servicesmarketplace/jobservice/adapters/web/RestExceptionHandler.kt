package com.wks.servicesmarketplace.jobservice.adapters.web

import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorType
import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorResponse
import org.axonframework.messaging.interceptors.JSR303ViolationException
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
        val validationException =  ex as JSR303ViolationException
        val errorResponse = ErrorResponse(
                ErrorType.VALIDATION,
                "Validation Failed",
                validationException.violations.map { it.propertyPath.toString() to it.message }.toMap()
        )
        return handleExceptionInternal(ex!!, errorResponse,
                HttpHeaders(), errorResponse.type.httpStatus, request!!)
    }
}