package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.customerservice.adapters.graphql.GraphQLUseCaseError;
import com.wks.servicemarketplace.customerservice.adapters.graphql.SanitizedError;
import com.wks.servicemarketplace.customerservice.core.exceptions.CoreException;
import com.wks.servicemarketplace.customerservice.core.exceptions.ErrorType;
import com.wks.servicemarketplace.customerservice.core.exceptions.ValidationException;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

public class GraphQLDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
        final ExceptionWhileDataFetching fetchError = new ExceptionWhileDataFetching(handlerParameters.getPath(), handlerParameters.getException(), handlerParameters.getSourceLocation());
        final GraphQLError error = getGraphQLError(fetchError);
        return DataFetcherExceptionHandlerResult.newResult(error).build();
    }

    private GraphQLError getGraphQLError(ExceptionWhileDataFetching fetchError) {
        if (fetchError.getException() instanceof CoreException) {
            return new GraphQLUseCaseError((CoreException) fetchError.getException(), fetchError);
        }
        if (fetchError.getException().getCause() instanceof CoreException) {
            return new GraphQLUseCaseError((CoreException) fetchError.getException().getCause(), fetchError);
        }
        if (fetchError.getException() instanceof ValidationException) {
            final ValidationException validationException = (ValidationException) fetchError.getException();
            return new GraphQLUseCaseError(new CoreException(
                    ErrorType.VALIDATION,
                    validationException.getMessage(),
                    validationException.getFields(),
                    validationException
            ), fetchError);
        }
        return new SanitizedError(fetchError);
    }
}
