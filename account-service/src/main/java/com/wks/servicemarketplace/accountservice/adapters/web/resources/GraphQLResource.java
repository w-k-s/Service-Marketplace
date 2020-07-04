package com.wks.servicemarketplace.accountservice.adapters.web.resources;

import com.wks.servicemarketplace.accountservice.config.GraphQLContext;
import graphql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class GraphQLResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLResource.class);

    private final GraphQL graphQL;
    private final String introspectionQuery;

    @Inject
    public GraphQLResource(GraphQLContext context) {
        LOGGER.info("Initializing GraphQLResource");
        this.graphQL = GraphQL.newGraphQL(context.getGraphQLSchema()).build();
        this.introspectionQuery = context.getIntrospectionQuery();
    }

    @GET
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@QueryParam("query") final String query) {
        ExecutionResult data = filterGraphQLErrors(graphQL.execute(query));
        return Response.ok(data).build();
    }

    @POST
    @Path("/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response mutation(final GraphQLRequest graphQLRequest) {
        ExecutionResult result = filterGraphQLErrors(graphQL.execute(
                graphQLRequest.getQuery(),
                graphQLRequest.getOperationName(),
                null,
                graphQLRequest.getVariables()));
        return Response.ok(result).build();
    }

    @GET
    @Path("/graphql/schema.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response graphQLSchema() {
        ExecutionResult result = filterGraphQLErrors(graphQL.execute(introspectionQuery));
        return Response.ok(result).build();
    }

    private ExecutionResult filterGraphQLErrors(ExecutionResult executionResult) {
        if (executionResult.getErrors().isEmpty()) {
            return executionResult;
        }
        List<GraphQLError> graphQLErrorList = new ArrayList<>();
        for (GraphQLError error : executionResult.getErrors()) {
            LOGGER.error("ERROR-{}: {}", error.getClass(), error.getMessage());
            if (error instanceof ExceptionWhileDataFetching) {
                graphQLErrorList.add(new SanitizedError((ExceptionWhileDataFetching) error));
            } else {
                graphQLErrorList.add(error);
            }
        }
        return new ExecutionResultImpl(executionResult.getData(), graphQLErrorList, executionResult.getExtensions());
    }
}
