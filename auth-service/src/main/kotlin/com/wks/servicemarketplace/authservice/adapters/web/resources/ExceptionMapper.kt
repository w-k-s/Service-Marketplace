package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.http.ErrorResponse
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

internal fun CoreException.toResponse(): Response {
    return Response
            .status(errorType.code)
            .entity(ErrorResponse(
                    this.errorType,
                    this.message,
                    this.details
            ))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build()
}

internal fun Throwable.toResponse(): Response {
    return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(ErrorResponse(
                    ErrorType.UNKNOWN,
                    this.message,
                    emptyMap()
            ))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build()
}
