package com.wks.servicemarketplace.accountservice.adapters.graphql;

import graphql.ExecutionResult;
import graphql.GraphQLError;

import java.util.List;
import java.util.Map;

/**
 * Apollo-server considers errors: [] to be an error.
 * This causes the service to not be detected.
 * As a workaround, ExecutionResult from GraphQL is wrapped in ApolloExecutionResult.
 * ApolloExecutionResult returns null when the errors array is empty.
 *
 * https://github.com/apollographql/apollo-client/issues/156
 */
public class ApolloExecutionResult implements ExecutionResult {

    private ExecutionResult executionResult;

    private ApolloExecutionResult(ExecutionResult executionResult) {
        this.executionResult = executionResult;
    }

    public static ExecutionResult of(ExecutionResult executionResult) {
        return new ApolloExecutionResult(executionResult);
    }

    @Override
    public List<GraphQLError> getErrors() {
        return executionResult == null
                || executionResult.getErrors() == null
                || executionResult.getErrors().isEmpty()
                ? null
                : executionResult.getErrors();
    }

    @Override
    public <T> T getData() {
        return executionResult == null ? null : executionResult.getData();
    }

    @Override
    public boolean isDataPresent() {
        return executionResult != null && executionResult.isDataPresent();
    }

    @Override
    public Map<Object, Object> getExtensions() {
        return executionResult == null ? null : executionResult.getExtensions();
    }

    @Override
    public Map<String, Object> toSpecification() {
        return executionResult == null ? null : executionResult.toSpecification();
    }
}