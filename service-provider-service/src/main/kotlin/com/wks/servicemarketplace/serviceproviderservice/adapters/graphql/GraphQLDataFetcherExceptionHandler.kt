package com.wks.servicemarketplace.serviceproviderservice.adapters.graphql

import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CoreException
import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult

class GraphQLDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        return handlerParameters.let {
            ExceptionWhileDataFetching(it.path, it.exception, it.sourceLocation)
        }.let {
            when (it) {
                is CoreException -> (it.exception as CoreException).toDataFetcherExceptionHandlerResult()
                else -> DataFetcherExceptionHandlerResult.newResult(GraphQLSanitizedException(it)).build()
            }
        }
    }
}

fun CoreException.toDataFetcherExceptionHandlerResult(): DataFetcherExceptionHandlerResult {
    return DataFetcherExceptionHandlerResult.newResult(
            GraphQLCoreException(this.message, this.fields, this.errorType)
    ).build()
}