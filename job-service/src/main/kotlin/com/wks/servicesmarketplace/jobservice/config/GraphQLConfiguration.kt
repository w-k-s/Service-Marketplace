package com.wks.servicesmarketplace.jobservice.config

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.fasterxml.jackson.databind.ObjectMapper
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
import graphql.execution.instrumentation.Instrumentation
import graphql.kickstart.execution.error.GraphQLErrorHandler
import graphql.kickstart.tools.ObjectMapperConfigurer
import graphql.kickstart.tools.SchemaParserOptions
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.modelling.command.AggregateNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component


@Component
class GraphQLConfiguration(private val dataFetchers: GraphQLDataFetchers) {


    @Bean
    fun graphQL(@Value("classpath:schema.graphqls") sdl: Resource): GraphQL {
        val schema = Federation.transform(sdl.file, createRuntimeWiring()).build()
        return GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(AsyncExecutionStrategy(GraphQLDataFetcherExceptionHandler()))
                .mutationExecutionStrategy(AsyncExecutionStrategy(GraphQLDataFetcherExceptionHandler()))
                .instrumentation(
                        FederatedTracingInstrumentation(FederatedTracingInstrumentation.Options(true))
                ).build()
    }

    fun createRuntimeWiring(): RuntimeWiring {
        return RuntimeWiring
                .newRuntimeWiring()
                .type("Query") { builder ->
                    builder.dataFetcher("getServiceOrderById", dataFetchers.getServiceOrderByIdDataFetcher)
                }
                .type("Mutation") { builder ->
                    builder.dataFetcher("createServiceOrder", dataFetchers.createServiceOrderDataFetcher)
                }.build()
    }
}