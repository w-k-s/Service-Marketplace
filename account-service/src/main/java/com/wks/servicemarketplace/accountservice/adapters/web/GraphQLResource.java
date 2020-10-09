package com.wks.servicemarketplace.accountservice.adapters.web;

import com.wks.servicemarketplace.accountservice.adapters.graphql.ApolloExecutionResult;
import com.wks.servicemarketplace.accountservice.adapters.graphql.GraphQLRequest;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/")
@RequestScoped
public class GraphQLResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLResource.class);

    private final GraphQL graphQL;

    @Inject
    public GraphQLResource(GraphQL graphQL) {
        LOGGER.info("Initializing GraphQLResource");
        this.graphQL = graphQL;
    }

    @GET
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@QueryParam("query") final String query,
                          @Context SecurityContext securityContext) {
        final ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .context(securityContext.getUserPrincipal())
                .build();
        ExecutionResult data = ApolloExecutionResult.of(graphQL.execute(executionInput));
        return Response.ok(data).build();
    }

    @POST
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response mutation(final GraphQLRequest graphQLRequest,
                             @Context SecurityContext securityContext) {
        final ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .operationName(graphQLRequest.getOperationName())
                .variables(graphQLRequest.getVariables())
                .context(securityContext.getUserPrincipal())
                .build();

        ExecutionResult result = ApolloExecutionResult.of(graphQL.execute(executionInput));
        return Response.ok(result).build();
    }
}
