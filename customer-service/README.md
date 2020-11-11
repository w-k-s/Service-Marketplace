# Customer Service

This service uses Jersey in a Jetty Container with JOOQ/JDBC.

The architecture of this service is, for the most part, my preferred architecture for developing Java backends. 
The key points are:

**1. No Aspect-Oriented Programming**

- Aspect-Oriented Programming makes it difficult to know how/when a method is called or if it is called at all.
- The worst offender is the  `@Transactional` annotation. One might reasonably assume that annotating a method with `@Transactional` causes the method to execute within a transaction.
- However, this is only true if the `@Transactional` method is invoked from a bean. 

**2. No JPA/Hibernate**
- JPA, in my opinion, has excessive overhead.
- Something like JOOQ which is a DSL for building queries and optionally executing them has less overhead, is more intuitive, and is flexible enough to allow you to leverage JDBC if needed.

**3. Minimal Magic**
- What I dislike about Spring is it's magic. Put this dependency in and *snap* it SHOULD just works.
- The problem is when it doesn't work and you don't get useful error messages. When this happens, You don't know which part of your code is causing the issue and you don't know what exactly to ask (so Stackoverflow closes your question).
- Debugging such problems requires experience/intuition rather than reasoning and deduction.
- I prefer to have things as explicit as possible rather than annotation/reflection magic. When things go wrong, I want to be able to see where they went wrong in MY project rather than a huge irrelevant stacktrace that you get from Spring.

- I would have preferred to have used constructor-based Dependency Injection in this project but I couldn't do so consistently so I went with the `bindFactory` approach.

**4. Toolkits over Frameworks**
- Frameworks have to be general purpose, backwards compatible and flexible enough to adapt to changes in the programming world (e.g. OOP -> Functional,Rx programming).
- As a result of this, when frameworks solve a problem they have to solve them in as flexible a way as possible. This can lead to complexity and bloat.
- What I find is that it's a lot simpler to take a minimal framework and to either import a toolkit or implement utilities that reduce the boilerplate specific to your needs.
- For example, Spring's `@Transactional` reduces the boilerplate in managing a transaction. However, it has a dependency on Spring's AOP library. Is it worth it?
- The `TransactionUtils` class in this project accomplishes the same thing but in a few lines of code that is specific to my needs. It does not need any extra library and certainly not one that uses reflection and proxying.

## Database Setup

### On Local Machine

1. Connect to your PostgreSQL database and create the `customer` database using the command `CREATE DATABASE customer`.

    ```shell script
    $ psql
    psql (11.5)
    Type "help" for help.
    
    =# CREATE DATABASE customer
    CREATE DATABASE
    ```
2. Create a `gradle.properties` file in the same directory as `build.gradle`.
3. Provide the following details in the `gradle.properties` file.

    ```
    liquibaseTaskPrefix=liquibase
    mainUrl=jdbc:postgresql://localhost:5432/customer
    username=<DB username>
    password=<DB Password>
    runList=main
    ```
    **NOTE**: This project uses the liquibase gradle plugin. This plugin creates a Gradle task for each command supported by Liquibase. The `liquibaseTaskPrefix` option will tell the liquibase plugin to capitalize the task name and prefix it with the given prefix. For example, if you put `liquibaseTaskPrefix=liquibase` in `gradle.properties`, then this plugin will create tasks named `liquibaseUpdate`, `liquibaseTag`. `liquibaseTaskPrefix` is optional, but recommended to avoid gradle task name collisions.

4. At this point, you've created the database and provided the connection parameters and liquibase configurations in the `gradle.properties` file.
You can now run the `liquibaseUpdate` command to set up the database:

    ```shell script
    ./gradlew liquibaseUpdate
    ```
    
    In case you did not set liquibaseTaskPrefix in the `gradle.properties` file, you would run:
    
    ```shell script
    ./gradlew update
    ```
   
   You can overwrite the parameters in the `gralde.properties` file from the command line
   
   ```shell script
    ./gradlew liquibaseUpdate -PmainUrl=jdbc:postgresql://localhost:5432/customer -Pusername=john.doe -Ppassword=123456 -PrunList=main 
    ```
   
## Environment Variables

| Environmental Variable | Description                       | Example                                                                            | required |
|------------------------|-----------------------------------|------------------------------------------------------------------------------------|----------|
| jdbcUrl                | JDBC URL of service database      | jdbc:postgresql://localhost:5432/customer                                          | true     |
| jdbcUsername           | service database username         |                                                                                    | true     |
| jdbcPassword           | service database password         |                                                                                    | true     |
| amqpHost               | RabbitMQ Broker Host              | localhost                                                                          | true     |
| amqpPort               | RabbitMQ Broker port              | 5672                                                                               | true     |
| serverHost             | Listen to requests from host      | http://localhost                                                                   | true     |
| serverPort             | Listen to requests for port       | 8081                                                                               | true     |

## Sample Requests

**Create Customer**
```
curl --location --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{
  "query": "mutation createCustomer($firstName: String!, $lastName: String!) { createCustomer(firstName: $firstName, lastName: $lastName) { uuid } }",
  "variables":{
	  "firstName": "Waqqas",
		"lastName": "Waqqas"
  }
}'
```

**Add Address**
```
curl --location --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{
	"query": "mutation { createAddress(address: {name: \"Home\", line1: \"Num 4, Privet Drive\", city: \"London\", country: \"UK\", latitude: 51.691336, longitude: -0.416966, customerExternalId: 1}) { uuid externalId name line1 line2 city country version } }"
}'
```

## Useful Resources

> 3.4. Life-cycle of Root Resource Classes
> By default the life-cycle of root resource classes is per-request which, namely that a new instance of a root resource class is created every time the request URI path matches the root resource. 
> This makes for a very natural programming model where constructors and fields can be utilized without concern for multiple concurrent requests to the same resource.
>  
> In general this is unlikely to be a cause of performance issues. 
> Class construction and garbage collection of JVMs has vastly improved over the years and many objects will be created and discarded to serve and process the HTTP request and return the HTTP response. 
- [Life-cycle of Root Resource Classes](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2692)

> 4.2.2. When to use Geography Data type over Geometry data type
> 
> If your data is contained in a small area, you might find that choosing an appropriate projection and using GEOMETRY is the best solution, in terms of performance and functionality available.
>
> If your data is global or covers a continental region, you may find that GEOGRAPHY allows you to build a system without having to worry about projection details. You store your data in longitude/latitude, and use the functions that have been defined on GEOGRAPHY. 
>
> If you don't understand projections, and you don't want to learn about them, and you're prepared to accept the limitations in functionality available in GEOGRAPHY, then it might be easier for you to use GEOGRAPHY than GEOMETRY. Simply load your data up as longitude/latitude and go from there.
>
- [4.2.2. When to use Geography Data type over Geometry data type](https://postgis.net/docs/manual-2.1/using_postgis_dbmanagement.html#PostGIS_GeographyVSGeometry)

  I went with option number 3 since learning about projections wasn't my main focus when working on this side-project.

- [Dependency Injection using Jersey's HK2](https://riptutorial.com/jersey/example/23632/basic-dependency-injection-using-jersey-s-hk2)
- [Implementing Custom Injection Provider](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/ioc.html#d0e17204)
- [A minimal REST API in Java](https://notes.eatonphil.com/a-minimal-rest-api-in-java.html)

> proxy cause a dynamic proxy to be created. So every time you access the service, it will be a proxy. 
> the proxyForSameScope just says that if the parent is in the same scope, i.e a request scope, don't make it a proxy, just use the real object. For instance, now it will be a proxy object, but if you try to inject it into a resource class (which is request scope by default), it will the the actual instance.
- [How to use RequestScoped objects within singleton jersey?](https://stackoverflow.com/a/41633475/821110)

> When using @Singleton on a resource class, you would expect the request scoped injection (without proxying solution provided aboce) to fail on start up, since there is no request on startup.
> 
>The reason is that @Singleton resources are not created until the first request. 
> So there actually is a request scope during injection. 
> 
> So any request sensitive information is injected with the service only once. 
> Now all subsequent request will see all the first request’s context sensitive information.
> I strongly suggest staying away from the @Singleton annotation. If you want a singleton, just register the instance in your ResourceConfig
- [Request Scoped Injection into a Singleton with Jersey](https://psamsotha.github.io/jersey/2015/12/16/request-scope-into-singleton-scope.html)