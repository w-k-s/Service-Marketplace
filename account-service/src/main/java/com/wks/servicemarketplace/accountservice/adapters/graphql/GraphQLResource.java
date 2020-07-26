package com.wks.servicemarketplace.accountservice.adapters.graphql;

import com.google.common.collect.ImmutableMap;
import com.wks.servicemarketplace.accountservice.core.exceptions.AuthenticationRequiredException;
import com.wks.servicemarketplace.accountservice.core.exceptions.UnauthorizedException;
import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import graphql.*;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

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
        ExecutionResult data = graphQL.execute(executionInput);
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

        ExecutionResult result = graphQL.execute(executionInput);
        return Response.ok(result).build();
    }
}
