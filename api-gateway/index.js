const { ApolloServer } = require("apollo-server");
const { ApolloGateway, RemoteGraphQLDataSource } = require("@apollo/gateway");

class AuthenticatedDataSource extends RemoteGraphQLDataSource{
    willSendRequest({request, context}){
        if(context && context.authHeaderValue){
            request.http.headers.set('Authorization', context.authHeaderValue);
        }
    }
}

const gateway = new ApolloGateway({
    serviceList: [
        {name: "auth-service", url: "http://localhost:8082/graphql"},
        {name: "account-service", url: "http://localhost:8081/graphql"},
        {name: "job-service", url: "http://localhost:8080/graphql"}
    ],
    buildService({name, url}){
        return new AuthenticatedDataSource({url})
    }
});

const server = new ApolloServer({
    gateway,
    subscriptions: false,
    debug: false, // disables stacktrace
    context: ({req}) => ({
        authHeaderValue: req.headers.authorization
    })
});

server.listen().then(({url})=>{
    console.log(`Server read at ${url}`)
});