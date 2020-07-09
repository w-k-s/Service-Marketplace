package com.wks.servicesmarketplace.jobservice.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.wks.servicesmarketplace.jobservice.adapters.graphql.GraphQLSanitizedException
import com.wks.servicesmarketplace.jobservice.adapters.graphql.GraphQLUseCaseError
import com.wks.servicesmarketplace.jobservice.core.exceptions.ErrorType
import com.wks.servicesmarketplace.jobservice.core.exceptions.InvalidStateTransitionException
import com.wks.servicesmarketplace.jobservice.core.exceptions.UseCaseException
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.GraphQLError
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.servlet.GraphQLErrorHandler
import graphql.servlet.ObjectMapperConfigurer
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.modelling.command.AggregateNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class GraphQLConfiguration {

    @Bean
    fun objectMapperConfigurer(): ObjectMapperConfigurer {
        return ObjectMapperConfigurer { objectMapper ->
            objectMapper.registerModule(JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }

    @Bean
    fun graphQLErrorHandler(): GraphQLErrorHandler {
        return GraphQLErrorHandler { errors ->
            val processedErrors = mutableListOf<GraphQLError>()
            for (error in errors) {
                if (error !is ExceptionWhileDataFetching) {
                    processedErrors.add(error)
                    continue
                }

                processedErrors.add(mapExceptionWhileDataFetching(error))
            }

            processedErrors
        }
    }

    private fun mapExceptionWhileDataFetching(error: ExceptionWhileDataFetching): GraphQLError {
        return when (error.exception) {
            is UseCaseException -> {
                val e = error.exception as UseCaseException
                GraphQLUseCaseError(e.message!!, e.userInfo, e.errorType)
            }
            is JSR303ViolationException -> {
                val e = error.exception as JSR303ViolationException
                val message = e.violations.joinToString(";") { "${it.propertyPath.toString()}: ${it.message}" }
                val userInfo = e.violations.map { it.propertyPath.toString() to it.message }.toMap()
                GraphQLUseCaseError(message, userInfo, ErrorType.VALIDATION)
            }
            is AggregateNotFoundException -> {
                val e = error.exception as AggregateNotFoundException
                val userInfo = mapOf("resourceId" to e.aggregateIdentifier.toString())
                GraphQLUseCaseError(e.message ?: "", userInfo, ErrorType.NOT_FOUND)
            }
            is InvalidStateTransitionException -> {
                val e = error.exception as InvalidStateTransitionException
                val userInfo = mapOf("type" to e.aggregate.simpleName!!, "from" to e.from, "to" to e.to)
                GraphQLUseCaseError(error.message, userInfo, ErrorType.INVALID_STATE)
            }
            else -> GraphQLSanitizedException(error)
        }
    }
}