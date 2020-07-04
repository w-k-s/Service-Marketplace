package com.wks.servicemarketplace.accountservice.config;

import graphql.schema.GraphQLSchema;

public class GraphQLContext {
    private final GraphQLSchema graphQLSchema;
    private final String introspectionQuery;

    public GraphQLContext(GraphQLSchema graphQLSchema, String introspectionQuery) {
        this.graphQLSchema = graphQLSchema;
        this.introspectionQuery = introspectionQuery;
    }

    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }

    public String getIntrospectionQuery() {
        return introspectionQuery;
    }
}
