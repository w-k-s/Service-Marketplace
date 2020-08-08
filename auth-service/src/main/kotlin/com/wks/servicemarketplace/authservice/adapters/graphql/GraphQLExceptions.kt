package com.wks.servicemarketplace.authservice.adapters.graphql

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wks.servicemarketplace.authservice.adapters.errors.ErrorType
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.execution.ExecutionPath
import graphql.language.SourceLocation

class GraphQLSanitizedException(inner: ExceptionWhileDataFetching)
    : ExceptionWhileDataFetching(ExecutionPath.fromList(inner.path), inner.exception, inner.locations.first()) {

    @Override
    @JsonIgnore
    override fun getException(): Throwable {
        return super.getException()
    }
}

data class GraphQLUseCaseError(private val description: String,
                               private val userInfo: Map<String, String> = emptyMap(),
                               private val errorType: ErrorType) : GraphQLError {

    private val extensions = mutableMapOf<String,Any>()

    init{
        extensions["code"] = errorType.code
        extensions["type"] = errorType.name
        extensions["description"] = description
        extensions["userInfo"] = userInfo
    }

    override fun getMessage(): String = description

    override fun getErrorType() = graphql.ErrorType.DataFetchingException

    override fun getLocations() = emptyList<SourceLocation>()

    override fun getExtensions() = extensions
}