const { ApolloServer } = require("apollo-server");
const { ApolloGateway, RemoteGraphQLDataSource } = require("@apollo/gateway");

class AuthenticatedDataSource extends RemoteGraphQLDataSource{
    willSendRequest({request, context}){
        request.http.headers.set('Authorization', context.authHeaderValue);
    }
}

const gateway = new ApolloGateway({
    serviceList: [
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
    context: ({req}) => ({
        authHeaderValue: req.headers.authorization
    })
});

server.listen().then(({url})=>{
    console.log(`Server read at ${url}`)
});