package com.wks.servicesmarketplace.jobservice.config

import com.wks.servicesmarketplace.jobservice.adapters.graphql.GraphQLSanitizedException
import com.wks.servicesmarketplace.jobservice.adapters.graphql.GraphQLUseCaseError
import com.wks.servicesmarketplace.jobservice.core.exceptions.ErrorType
import com.wks.servicesmarketplace.jobservice.core.exceptions.InvalidStateTransitionException
import com.wks.servicesmarketplace.jobservice.core.exceptions.UseCaseException
import com.wks.servicesmarketplace.jobservice.core.utils.ValidationException
import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.modelling.command.AggregateNotFoundException

class GraphQLDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val error = ExceptionWhileDataFetching(handlerParameters.path, handlerParameters.exception, handlerParameters.sourceLocation)

        return when (error.exception) {
            is UseCaseException -> {
                val e = error.exception as UseCaseException
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(e.message!!, e.userInfo, e.errorType)
                ).build()
            }
            is JSR303ViolationException -> {
                val e = error.exception as JSR303ViolationException
                val message = e.violations.joinToString(";") { "${it.propertyPath.toString()}: ${it.message}" }
                val userInfo = e.violations.map { it.propertyPath.toString() to it.message }.toMap()
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(message, userInfo, ErrorType.VALIDATION)
                ).build()
            }
            is AggregateNotFoundException -> {
                val e = error.exception as AggregateNotFoundException
                val userInfo = mapOf("resourceId" to e.aggregateIdentifier.toString())
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(e.message ?: "", userInfo, ErrorType.NOT_FOUND)
                ).build()
            }
            is InvalidStateTransitionException -> {
                val e = error.exception as InvalidStateTransitionException
                val userInfo = mapOf("type" to e.aggregate.simpleName!!, "from" to e.from, "to" to e.to)
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, userInfo, ErrorType.INVALID_STATE)
                ).build()
            }
            is ValidationException -> {
                val e = error.exception as ValidationException
                DataFetcherExceptionHandlerResult.newResult(
                        GraphQLUseCaseError(error.message, e.fields, ErrorType.VALIDATION)
                ).build()
            }
            else -> DataFetcherExceptionHandlerResult.newResult(
                    GraphQLSanitizedException(error)
            ).build()
        }
    }
}