package com.wks.servicesmarketplace.orderservice.adapters.web

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.http.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class DefaultResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultResponseEntityExceptionHandler::class.java)
    }

    @ExceptionHandler(value = [CoreException::class])
    protected fun handleCoreException(ex: CoreException, request: WebRequest?): ResponseEntity<Any?>? {
        LOGGER.error("Unhandled Core Exception: ${ex.message}", ex)
        val body = ErrorResponse(
                ex.errorType,
                ex.message,
                ex.details ?: emptyMap()
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.valueOf(ex.errorType.code), request!!)
    }

    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        LOGGER.error("Handling HTTP Message Not Readable: ${ex.message}", ex)

        val cause = ex.cause
        val message = when(cause){
            is MissingKotlinParameterException -> createMissingKotlinParameterMessage(cause)
            else -> "Unable to read request object"
        }
        val body = ErrorResponse(
                ErrorType.INVALID_FORMAT,
                message
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.BAD_REQUEST, request!!)
    }

    private fun createMissingKotlinParameterMessage(cause: MissingKotlinParameterException): String {
        val name = cause.path.fold("") { jsonPath, ref ->
            val suffix = when {
                ref.index > -1 -> "[${ref.index}]"
                else -> ".${ref.fieldName}"
            }
            (jsonPath + suffix).removePrefix(".")
        }
        return "$name must not be null"
    }


    @ExceptionHandler(value = [Exception::class])
    protected fun handleThrowable(ex: Exception, request: WebRequest?): ResponseEntity<Any?>? {
        LOGGER.error("Unhandled exception: ${ex.message}", ex)
        val body = ErrorResponse(
                ErrorType.UNKNOWN,
                "Unknown exception"
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request!!)
    }
}