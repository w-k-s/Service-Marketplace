package com.wks.servicemarketplace.authservice.adapters.graphql

import com.wks.servicemarketplace.authservice.core.errors.*
import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult

class GraphQLDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val error = ExceptionWhileDataFetching(handlerParameters.path, handlerParameters.exception, handlerParameters.sourceLocation)

        return when (error.exception) {
            is CoreException -> (error.exception as CoreException).toDataFetcherExceptionHandlerResult()
            else -> DataFetcherExceptionHandlerResult.newResult(
                    GraphQLSanitizedException(error)
            ).build()
        }
    }
}

fun CoreException.toDataFetcherExceptionHandlerResult(): DataFetcherExceptionHandlerResult {
    return DataFetcherExceptionHandlerResult.newResult(
            GraphQLCoreException(this.message, this.fields ?: emptyMap(), this.errorType)
    ).build()
}