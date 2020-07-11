package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.adapters.graphql.AddressDataFetcher;
import com.wks.servicemarketplace.accountservice.adapters.graphql.CreateAddressDataFetcher;
import com.wks.servicemarketplace.accountservice.adapters.graphql.CreateCustomerDataFetcher;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import io.gqljf.federation.FederatedSchemaBuilder;
import io.gqljf.federation.tracing.FederatedTracingInstrumentation;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class GraphQLContextFactory implements Factory<GraphQLContext> {

    private GraphQLContext graphQLContext;

    @Inject
    public GraphQLContextFactory(CreateCustomerDataFetcher createCustomerDataFetcher,
                                 CreateAddressDataFetcher createAddressDataFetcher,
                                 AddressDataFetcher addressDataFetcher) throws IOException, URISyntaxException {
        final InputStream schemaInputStream = getClass().getClassLoader().getResourceAsStream("schema.graphqls");

        final GraphQLSchema transformedGraphQLSchema = new FederatedSchemaBuilder()
                .schemaInputStream(schemaInputStream)
                .runtimeWiring(createRuntimeWiring(createCustomerDataFetcher, createAddressDataFetcher, addressDataFetcher))
                .excludeSubscriptionsFromApolloSdl(true)
                .build();
        final GraphQL graphQL = GraphQL.newGraphQL(transformedGraphQLSchema)
                .instrumentation(new ChainedInstrumentation(Collections.singletonList(new FederatedTracingInstrumentation())))
                .build();

        String introspectionQuery = null;
        final URL introspectionQueryURL = getClass().getClassLoader().getResource("introspectionQuery.graphqls");
        if (introspectionQueryURL != null) {
            introspectionQuery = new String(Files.readAllBytes(Paths.get(introspectionQueryURL.toURI())));
        }
        this.graphQLContext = new GraphQLContext(
                graphQL,
                introspectionQuery
        );
    }

    private RuntimeWiring createRuntimeWiring(CreateCustomerDataFetcher createCustomerDataFetcher,
                                              CreateAddressDataFetcher createAddressDataFetcher, AddressDataFetcher addressDataFetcher) {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("address", addressDataFetcher))
                .type("Mutation", builder ->
                        builder.dataFetcher("createCustomer", createCustomerDataFetcher)
                                .dataFetcher("createAddress", createAddressDataFetcher))
                .build();
    }

    @Override
    public GraphQLContext provide() {
        return graphQLContext;
    }

    @Override
    public void dispose(GraphQLContext instance) {
    }
}
