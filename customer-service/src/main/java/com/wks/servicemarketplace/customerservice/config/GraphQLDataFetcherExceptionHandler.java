package com.wks.servicemarketplace.customerservice.config;

import com.google.common.collect.ImmutableMap;
import com.wks.servicemarketplace.customerservice.adapters.graphql.GraphQLUseCaseError;
import com.wks.servicemarketplace.customerservice.adapters.graphql.SanitizedError;
import com.wks.servicemarketplace.customerservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.customerservice.core.exceptions.UnauthorizedException;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.customerservice.core.usecase.errors.ErrorType;
import com.wks.servicemarketplace.customerservice.core.utils.ValidationException;
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
        if (fetchError.getException() instanceof UseCaseException) {
            return new GraphQLUseCaseError((UseCaseException) fetchError.getException(), fetchError);
        }
        if (fetchError.getException().getCause() instanceof UseCaseException) {
            return new GraphQLUseCaseError((UseCaseException) fetchError.getException().getCause(), fetchError);
        }
        if (fetchError.getException() instanceof AuthenticationRequiredException) {
            return new GraphQLUseCaseError(new UseCaseException(
                    ErrorType.UNAUTHENTICATED,
                    fetchError.getException()
            ), fetchError);
        }
        if (fetchError.getException() instanceof UnauthorizedException) {
            return new GraphQLUseCaseError(new UseCaseException(
                    ErrorType.UNAUTHORIZED,
                    fetchError.getMessage(),
                    ImmutableMap.of("requiredRole", ((UnauthorizedException) fetchError.getException()).getRequiredRole()),
                    fetchError.getException()
            ), fetchError);
        }
        if (fetchError.getException() instanceof ValidationException) {
            final ValidationException validationException = (ValidationException) fetchError.getException();
            return new GraphQLUseCaseError(new UseCaseException(
                    ErrorType.VALIDATION,
                    validationException.getMessage(),
                    validationException.getFields(),
                    validationException
            ), fetchError);
        }
        return new SanitizedError(fetchError);
    }
}
