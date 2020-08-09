package com.wks.servicemarketplace.authservice.adapters.web.resources

import com.wks.servicemarketplace.authservice.adapters.graphql.ApolloExecutionResult
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class GraphQLResource @Inject constructor(private val graphQL: GraphQL) {

    @POST
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    fun post(request: GraphQLRequest): Response {
        val executionInput = ExecutionInput.newExecutionInput()
                .operationName(request.operationName)
                .query(request.query)
                .variables(request.variables ?: emptyMap())
                .build()

        val data: ExecutionResult = ApolloExecutionResult(graphQL.execute(executionInput))
        return Response.ok(data).build()
    }

    @GET
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    fun get(query: String): Response {
        val data: ExecutionResult = ApolloExecutionResult(graphQL.execute(query))
        return Response.ok(data).build()
    }
}