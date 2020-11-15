package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.serviceproviderservice.adapters.graphql.GraphQLDataFetcherExceptionHandler
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.idl.RuntimeWiring
import io.gqljf.federation.FederatedSchemaBuilder
import io.gqljf.federation.tracing.FederatedTracingInstrumentation
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class GraphQLFactory @Inject constructor() : Factory<GraphQL> {

    private val graphQL: GraphQL

    init {
        val schemaInputStream = javaClass.classLoader.getResourceAsStream("schema.graphqls")

        val transformedGraphQLSchema = FederatedSchemaBuilder()
                .schemaInputStream(schemaInputStream)
                .runtimeWiring(createRuntimeWiring())
                .excludeSubscriptionsFromApolloSdl(true)
                .build()
        graphQL = GraphQL.newGraphQL(transformedGraphQLSchema)
                .queryExecutionStrategy(AsyncExecutionStrategy(GraphQLDataFetcherExceptionHandler()))
                .mutationExecutionStrategy(AsyncExecutionStrategy(GraphQLDataFetcherExceptionHandler()))
                .instrumentation(ChainedInstrumentation(listOf(FederatedTracingInstrumentation())))
                .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring? {
        return RuntimeWiring.newRuntimeWiring()
                .build()
    }

    override fun provide() = graphQL

    override fun dispose(instance: GraphQL?) {}
}