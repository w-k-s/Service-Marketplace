package com.wks.servicesmarketplace.jobservice.config

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.wks.servicesmarketplace.jobservice.adapters.graphql.GraphQLDataFetcherExceptionHandler
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.schema.idl.RuntimeWiring
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.BufferedReader


@Component
class GraphQLConfiguration(private val dataFetchers: GraphQLDataFetchers) {


    @Bean
    fun graphQL(): GraphQL {
        val sdl = javaClass.classLoader.getResourceAsStream("schema.graphqls")!!.bufferedReader().use(BufferedReader::readText)
        val schema = Federation.transform(sdl, createRuntimeWiring()).build()
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