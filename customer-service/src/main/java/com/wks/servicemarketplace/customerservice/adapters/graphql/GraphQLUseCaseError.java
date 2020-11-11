package com.wks.servicemarketplace.customerservice.adapters.graphql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wks.servicemarketplace.customerservice.core.exceptions.CoreException;
import com.wks.servicemarketplace.customerservice.core.exceptions.ErrorType;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphQLUseCaseError extends ExceptionWhileDataFetching {

    private static final String EXTENSION_ERROR_CODE = "code";
    private static final String EXTENSION_ERROR_TYPE = "type";
    private static final String EXTENSION_ERROR_MESSAGE = "description";
    private static final String EXTENSION_ERROR_FIELDS = "userInfo";

    private final String message;
    private final Map<String, Object> extensions;

    public GraphQLUseCaseError(CoreException useCaseException,
                               ExceptionWhileDataFetching cause) {
        super(ExecutionPath.fromList(cause.getPath()), useCaseException, cause.getLocations().get(0));
        this.message = useCaseException.getDescription();

        final ErrorType errorType = useCaseException.getErrorType();
        Map<String, Object> allExtensions = new HashMap<>();
        allExtensions.put(EXTENSION_ERROR_CODE, errorType.code);
        allExtensions.put(EXTENSION_ERROR_TYPE, errorType.name());
        allExtensions.put(EXTENSION_ERROR_MESSAGE, message);
        if (useCaseException.getUserInfo() != null && !useCaseException.getUserInfo().isEmpty()) {
            allExtensions.put(EXTENSION_ERROR_FIELDS, useCaseException.getUserInfo());
        }
        this.extensions = Collections.unmodifiableMap(allExtensions);
    }

    @Override
    @JsonProperty
    public String getMessage() {
        return message;
    }

    @Override
    @JsonProperty
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    @JsonProperty
    public graphql.ErrorType getErrorType() {
        return graphql.ErrorType.DataFetchingException;
    }

    @JsonProperty
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }
}
