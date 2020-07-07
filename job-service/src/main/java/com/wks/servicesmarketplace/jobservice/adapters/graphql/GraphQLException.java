package com.wks.servicesmarketplace.jobservice.adapters.graphql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wks.servicesmarketplace.jobservice.adapters.web.error.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphQLException extends RuntimeException implements GraphQLError {

    private static final String EXTENSION_ERROR_CODE = "code";
    private static final String EXTENSION_ERROR_TYPE = "type";
    private static final String EXTENSION_ERROR_MESSAGE = "description";
    private static final String EXTENSION_ERROR_FIELDS = "userInfo";

    private final String message;
    private final Map<String, Object> extensions;

    public GraphQLException(String message, ErrorType errorType) {
        this(message, Collections.emptyMap(), errorType);
    }

    public GraphQLException(String message, Map<String, Object> extensions, ErrorType errorType) {
        super(message);
        this.message = message;

        Map<String, Object> allExtensions = new HashMap<>();
        allExtensions.put(EXTENSION_ERROR_CODE, errorType.getCode());
        allExtensions.put(EXTENSION_ERROR_TYPE, errorType.name());
        allExtensions.put(EXTENSION_ERROR_MESSAGE, message);
        if (extensions != null && !extensions.isEmpty()) {
            allExtensions.put(EXTENSION_ERROR_FIELDS, extensions);
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

    @Override
    @JsonProperty
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    @JsonIgnore
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
