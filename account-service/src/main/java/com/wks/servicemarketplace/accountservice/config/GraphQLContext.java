package com.wks.servicemarketplace.accountservice.config;

import graphql.GraphQL;

public class GraphQLContext {
    private final GraphQL graphQL;
    private final String introspectionQuery;

    public GraphQLContext(GraphQL graphQL, String introspectionQuery) {
        this.graphQL = graphQL;
        this.introspectionQuery = introspectionQuery;
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

    public String getIntrospectionQuery() {
        return introspectionQuery;
    }
}
