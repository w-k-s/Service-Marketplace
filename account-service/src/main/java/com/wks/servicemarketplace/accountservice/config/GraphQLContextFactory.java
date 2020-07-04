package com.wks.servicemarketplace.accountservice.config;

import com.coxautodev.graphql.tools.SchemaParser;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.Mutation;
import com.wks.servicemarketplace.accountservice.adapters.web.resources.Query;
import com.wks.servicemarketplace.accountservice.core.usecase.address.AddAddressUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.address.FindAddressByCustomerUuidUseCase;
import com.wks.servicemarketplace.accountservice.core.usecase.customer.CreateCustomerUseCase;
import graphql.schema.GraphQLSchema;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphQLContextFactory implements Factory<GraphQLContext> {

    private GraphQLContext graphQLContext;

    @Inject
    public GraphQLContextFactory(FindAddressByCustomerUuidUseCase findAddressByCustomerUuidUseCase,
                                 CreateCustomerUseCase createCustomerUseCase,
                                 AddAddressUseCase addAddressUseCase) throws IOException, URISyntaxException {

        final GraphQLSchema graphQLSchema = SchemaParser.newParser()
                .files("schema.graphqls")
                .resolvers(new Query(
                        findAddressByCustomerUuidUseCase
                ), new Mutation(
                        createCustomerUseCase,
                        addAddressUseCase
                ))
                .build()
                .makeExecutableSchema();

        String introspectionQuery = null;
        final URL introspectionQueryURL = getClass().getClassLoader().getResource("introspectionQuery.graphqls");
        if (introspectionQueryURL != null) {
            introspectionQuery = new String(Files.readAllBytes(Paths.get(introspectionQueryURL.toURI())));
        }
        this.graphQLContext = new GraphQLContext(
                graphQLSchema,
                introspectionQuery
        );
    }

    @Override
    public GraphQLContext provide() {
        return graphQLContext;
    }

    @Override
    public void dispose(GraphQLContext instance) {
    }
}
