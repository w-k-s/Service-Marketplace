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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    private ExecutionResult filterGraphQLErrors(ExecutionResult executionResult) {
        if (executionResult.getErrors().isEmpty()) {
            return executionResult;
        }
        List<GraphQLError> graphQLErrorList = new ArrayList<>();
        for (GraphQLError error : executionResult.getErrors()) {

            if (!(error instanceof ExceptionWhileDataFetching)) {
                graphQLErrorList.add(error);
                continue;
            }

            graphQLErrorList.add(getGraphQLError((ExceptionWhileDataFetching) error));
        }
        return new ExecutionResultImpl(executionResult.getData(), graphQLErrorList, executionResult.getExtensions());
    }

    private GraphQLError getGraphQLError(ExceptionWhileDataFetching fetchError) {
        if (fetchError.getException() instanceof UseCaseException) {
            return new GraphQLUseCaseError((UseCaseException) fetchError.getException(), fetchError);
        }
        if (fetchError.getException().getCause() instanceof UseCaseException) {
            return new GraphQLUseCaseError((UseCaseException) fetchError.getException().getCause(), fetchError);
        }
        if (fetchError.getException() instanceof AuthenticationRequiredException) {
            return new GraphQLUseCaseError(new UseCaseException(
                    ErrorType.UNAUTHENTICATED,
                    fetchError.getException()
            ), fetchError);
        }
        if (fetchError.getException() instanceof UnauthorizedException) {
            return new GraphQLUseCaseError(new UseCaseException(
                    ErrorType.UNAUTHORIZED,
                    fetchError.getMessage(),
                    ImmutableMap.of("requiredRole", ((UnauthorizedException) fetchError.getException()).getRequiredRole()),
                    fetchError.getException()
            ), fetchError);
        }
        return new SanitizedError(fetchError);
    }
}
