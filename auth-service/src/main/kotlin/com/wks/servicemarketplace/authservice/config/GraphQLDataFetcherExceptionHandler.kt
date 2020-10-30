package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.errors.ErrorType
import com.wks.servicemarketplace.authservice.adapters.graphql.GraphQLSanitizedException
import com.wks.servicemarketplace.authservice.adapters.graphql.GraphQLUseCaseError
import com.wks.servicemarketplace.authservice.adapters.utils.ValidationException
import com.wks.servicemarketplace.authservice.core.DuplicateUsernameException
import com.wks.servicemarketplace.authservice.core.UnauthorizedException
import com.wks.servicemarketplace.authservice.core.UserNotFoundException
import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult

class GraphQLDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val error = ExceptionWhileDataFetching(handlerParameters.path, handlerParameters.exception, handlerParameters.sourceLocation)

        return when (error.exception) {
            is ValidationException -> {
                val e = error.exception as ValidationException
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, e.fields, ErrorType.VALIDATION)
                ).build()
            }
            is UserNotFoundException -> {
                val e = error.exception as UserNotFoundException
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, mapOf("username" to e.username), ErrorType.NOT_FOUND)
                ).build()
            }
            is UnauthorizedException -> {
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, errorType = ErrorType.AUTHENTICATION)
                ).build()
            }
            is DuplicateUsernameException -> {
                val e = error.exception as DuplicateUsernameException
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, mapOf("username" to e.username), ErrorType.DUPLICATE_USERNAME)
                ).build()
            }
            else -> DataFetcherExceptionHandlerResult.newResult(
                    GraphQLSanitizedException(error)
            ).build()
        }
    }
}