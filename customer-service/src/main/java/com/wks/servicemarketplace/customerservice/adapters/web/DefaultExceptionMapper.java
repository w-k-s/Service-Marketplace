package com.wks.servicemarketplace.customerservice.adapters.web;

import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;
import com.wks.servicemarketplace.common.errors.HasErrorDetails;
import com.wks.servicemarketplace.common.http.ErrorResponse;
import com.wks.servicemarketplace.common.http.ErrorResponseKt;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof CoreException) {
            return createCoreErrorResponse((CoreException) exception);
        } else {
            return createThrowableErrorResponse(exception);
        }
    }

    private Response createThrowableErrorResponse(Throwable exception) {
        return Response
                .status(ErrorResponseKt.httpStatusCode(ErrorType.UNKNOWN))
                .entity(new ErrorResponse(
                        ErrorType.UNKNOWN,
                        exception.getMessage(),
                        null)
                ).build();
    }

    private Response createCoreErrorResponse(CoreException coreException) {
        final Map<String, List<String>> errorDetails;
        if (coreException instanceof HasErrorDetails) {
            errorDetails = ((HasErrorDetails) coreException).getErrorDetails();
        } else {
            errorDetails = Collections.emptyMap();
        }

        return Response
                .status(ErrorResponseKt.httpStatusCode(coreException.getErrorType()))
                .entity(new ErrorResponse(
                        coreException.getErrorType(),
                        coreException.getMessage(),
                        errorDetails)
                ).build();
    }
}
