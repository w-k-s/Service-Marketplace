package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.graphql.ApiTokenDataFetcher
import com.wks.servicemarketplace.authservice.adapters.graphql.GraphQLDataFetcherExceptionHandler
import com.wks.servicemarketplace.authservice.adapters.graphql.LoginDataFetcher
import com.wks.servicemarketplace.authservice.adapters.graphql.RegisterDataFetcher
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.idl.RuntimeWiring
import io.gqljf.federation.FederatedSchemaBuilder
import io.gqljf.federation.tracing.FederatedTracingInstrumentation
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class GraphQLFactory @Inject constructor(private val loginDataFetcher: LoginDataFetcher,
                                         private val registerDataFetcher: RegisterDataFetcher,
                                         private val apiTokenFetcher: ApiTokenDataFetcher) : Factory<GraphQL> {

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
                .type("Mutation") { builder ->
                    builder.dataFetcher("signIn", loginDataFetcher)
                            .dataFetcher("signUp", registerDataFetcher)
                            .dataFetcher("apiToken", apiTokenFetcher)
                }
                .build()
    }

    override fun provide() = graphQL

    override fun dispose(instance: GraphQL?) {}
}