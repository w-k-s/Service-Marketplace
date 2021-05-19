package com.wks.servicemarketplace.customerservice.adapters.web;

import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;
import com.wks.servicemarketplace.common.http.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("Error Response for Exception", exception);

        if (exception instanceof CoreException) {
            return createCoreErrorResponse((CoreException) exception);
        } else {
            return createThrowableErrorResponse(exception);
        }
    }

    private Response createThrowableErrorResponse(Throwable exception) {
        return Response
                .status(ErrorType.UNKNOWN.getCode())
                .entity(new ErrorResponse(
                        ErrorType.UNKNOWN,
                        exception.getMessage(),
                        null)
                ).build();
    }

    private Response createCoreErrorResponse(CoreException coreException) {
        return Response
                .status(coreException.getErrorType().getCode())
                .entity(new ErrorResponse(
                        coreException.getErrorType(),
                        coreException.getMessage(),
                        coreException.getDetails())
                ).build();
    }
}
