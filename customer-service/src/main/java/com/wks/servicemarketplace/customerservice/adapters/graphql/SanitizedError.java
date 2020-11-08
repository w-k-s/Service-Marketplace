package com.wks.servicemarketplace.customerservice.adapters.graphql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;

public class SanitizedError extends ExceptionWhileDataFetching {

    public SanitizedError(ExceptionWhileDataFetching inner) {
        super(ExecutionPath.fromList(inner.getPath()), inner.getException(), inner.getLocations().get(0));
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }
}
