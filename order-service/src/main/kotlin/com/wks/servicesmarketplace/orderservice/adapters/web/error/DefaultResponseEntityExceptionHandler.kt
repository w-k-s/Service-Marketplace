package com.wks.servicesmarketplace.orderservice.adapters.web.error

import com.wks.servicesmarketplace.orderservice.core.exceptions.CoreException
import com.wks.servicesmarketplace.orderservice.core.exceptions.CoreRuntimeException
import com.wks.servicesmarketplace.orderservice.core.exceptions.ErrorType
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
                ex.description,
                ex.userInfo
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), ex.errorType.httpCode(), request!!)
    }

    @ExceptionHandler(value = [CoreRuntimeException::class])
    protected fun handleCoreRuntimeException(ex: CoreRuntimeException, request: WebRequest?): ResponseEntity<Any?>? {
        val body = ErrorResponse(
                ex.errorType,
                ex.description,
                ex.userInfo
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), ex.errorType.httpCode(), request!!)
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

fun ErrorType.httpCode(): HttpStatus {
    return when (this) {
        ErrorType.UNAUTHENTICATED, ErrorType.USER_ID_MISSING -> HttpStatus.UNAUTHORIZED
        ErrorType.INSUFFICIENT_PRIVILEGES -> HttpStatus.FORBIDDEN
        ErrorType.VALIDATION, ErrorType.INVALID_COUNTRY -> HttpStatus.BAD_REQUEST
        ErrorType.ADDRESS_NOT_FOUND, ErrorType.NOT_FOUND, ErrorType.SERVICE_ORDER_NOT_FOUND -> HttpStatus.NOT_FOUND
        ErrorType.INVALID_STATE -> HttpStatus.CONFLICT
        ErrorType.UNKNOWN -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}