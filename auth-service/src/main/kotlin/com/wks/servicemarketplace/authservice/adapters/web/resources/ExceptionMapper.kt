package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.core.errors.CoreException
import com.wks.servicemarketplace.authservice.core.errors.ErrorType
import org.glassfish.jersey.server.spi.ResponseErrorMapper
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class DefaultExceptionMapper : ExceptionMapper<Throwable>, ResponseErrorMapper {
    override fun toResponse(ex: Throwable): Response {
        return when (ex) {
            is CoreException -> (ex as CoreException).toResponse()
            else -> ex.toResponse()
        }
    }
}

internal fun ErrorType.httpStatusCode(): Int {
    return when (this) {
        ErrorType.AUTHORIZATION -> Response.Status.FORBIDDEN.statusCode
        ErrorType.VALIDATION,
        ErrorType.DUPLICATE_USERNAME -> Response.Status.BAD_REQUEST.statusCode
        ErrorType.USER_NOT_FOUND -> Response.Status.NOT_FOUND.statusCode
        ErrorType.REGISTRATION_IN_PROGRESS -> 422 // Unprocessible entity
        ErrorType.UNKNOWN,
        ErrorType.LOGIN_FAILED,
        ErrorType.REGISTRATION_FAILED -> Response.Status.INTERNAL_SERVER_ERROR.statusCode
    }
}

internal fun CoreException.toResponse(): Response {
    return Response
            .status(errorType.httpStatusCode())
            .entity(CoreErrorResponse(
                    this.errorType,
                    this.message,
                    this.fields
            ))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build()
}

internal fun Throwable.toResponse(): Response {
    return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(CoreErrorResponse(
                    ErrorType.UNKNOWN,
                    this.message,
                    emptyMap()
            ))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build()
}

internal data class CoreErrorResponse(
        val errorType: ErrorType,
        val message: String?,
        val fields: Map<String, List<String>>? = emptyMap()
)