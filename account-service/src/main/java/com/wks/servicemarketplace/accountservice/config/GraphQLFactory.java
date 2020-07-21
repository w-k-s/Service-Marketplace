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
import java.io.InputStream;
import java.util.Collections;

public class GraphQLFactory implements Factory<GraphQL> {

    private GraphQL graphQL;

    @Inject
    public GraphQLFactory(CreateCustomerDataFetcher createCustomerDataFetcher,
                          CreateAddressDataFetcher createAddressDataFetcher,
                          AddressDataFetcher addressDataFetcher) {
        final InputStream schemaInputStream = getClass().getClassLoader().getResourceAsStream("schema.graphqls");

        final GraphQLSchema transformedGraphQLSchema = new FederatedSchemaBuilder()
                .schemaInputStream(schemaInputStream)
                .runtimeWiring(createRuntimeWiring(createCustomerDataFetcher, createAddressDataFetcher, addressDataFetcher))
                .excludeSubscriptionsFromApolloSdl(true)
                .build();
        this.graphQL = GraphQL.newGraphQL(transformedGraphQLSchema)
                .instrumentation(new ChainedInstrumentation(Collections.singletonList(new FederatedTracingInstrumentation())))
                .build();
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
    public GraphQL provide() {
        return graphQL;
    }

    @Override
    public void dispose(GraphQL instance) {
    }
}
