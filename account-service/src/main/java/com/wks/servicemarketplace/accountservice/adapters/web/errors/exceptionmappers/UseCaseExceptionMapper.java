package com.wks.servicemarketplace.accountservice.adapters.web.errors.exceptionmappers;

import com.wks.servicemarketplace.accountservice.adapters.web.errors.ErrorResponse;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UseCaseExceptionMapper implements ExceptionMapper<UseCaseException> {

    @Override
    public Response toResponse(UseCaseException exception) {
        return Response.status(getStatusCode(exception.getErrorType()))
                .entity(new ErrorResponse(
                        exception.getErrorType(),
                        "TODO: localized message",
                        exception.getUserInfo())
                )
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private int getStatusCode(ErrorType errorType) {
        switch (errorType) {
            case VALIDATION:
            case INVALID_STATE:
            case INVALID_COUNTRY:
                return HttpStatus.BAD_REQUEST_400;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND_404;
            case CUSTOMER_NOT_CREATED:
            case ADDRESS_NOT_CREATED:
            case UNKNOWN:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
        }
    }
}
