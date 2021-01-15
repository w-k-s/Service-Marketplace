package com.wks.servicesmarketplace.orderservice.adapters.web.error

import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.http.httpStatusCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class DefaultResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultResponseEntityExceptionHandler::class.java)
    }

    @ExceptionHandler(value = [CoreException::class])
    protected fun handleCoreException(ex: CoreException, request: WebRequest?): ResponseEntity<Any?>? {
        val body = ErrorResponse(
                ex.errorType,
                ex.message,
                ex.details ?: emptyMap()
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.valueOf(ex.errorType.httpStatusCode()), request!!)
    }

    @ExceptionHandler(value = [Exception::class])
    protected fun handleThrowable(ex: Exception, request: WebRequest?): ResponseEntity<Any?>? {
        LOGGER.error(ex.message, ex)
        val body = ErrorResponse(
                ErrorType.UNKNOWN,
                "Unknown exception"
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request!!)
    }
}