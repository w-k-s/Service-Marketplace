# Useful Resources

## Jersey

- [A minimal REST API in Java](https://notes.eatonphil.com/a-minimal-rest-api-in-java.html)

### Life-cycle of Root Resource Classes

> By default the life-cycle of root resource classes is per-request which, namely that a new instance of a root resource class is created every time the request URI path matches the root resource.
> This makes for a very natural programming model where constructors and fields can be utilized without concern for multiple concurrent requests to the same resource.
>
> In general this is unlikely to be a cause of performance issues.
> Class construction and garbage collection of JVMs has vastly improved over the years and many objects will be created and discarded to serve and process the HTTP request and return the HTTP response.

- [Life-cycle of Root Resource Classes](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2692)

### Depdency Injection using HK2

- [Dependency Injection using Jersey's HK2](https://riptutorial.com/jersey/example/23632/basic-dependency-injection-using-jersey-s-hk2)

- [Implementing Custom Injection Provider](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/ioc.html#d0e17204)

### Scopes

> proxy cause a dynamic proxy to be created. So every time you access the service, it will be a proxy.
> the proxyForSameScope just says that if the parent is in the same scope, i.e a request scope, don't make it a proxy, just use the real object. For instance, now it will be a proxy object, but if you try to inject it into a resource class (which is request scope by default), it will the the actual instance.

- [How to use RequestScoped objects within singleton jersey?](https://stackoverflow.com/a/41633475/821110)

> When using @Singleton on a resource class, you would expect the request scoped injection (without proxying solution provided aboce) to fail on start up, since there is no request on startup.
>
> The reason is that @Singleton resources are not created until the first request.
> So there actually is a request scope during injection.
>
> So any request sensitive information is injected with the service only once.
> Now all subsequent request will see all the first request’s context sensitive information.
> I strongly suggest staying away from the @Singleton annotation. If you want a singleton, just register the instance in your ResourceConfig

- [Request Scoped Injection into a Singleton with Jersey](https://psamsotha.github.io/jersey/2015/12/16/request-scope-into-singleton-scope.html)

## Validation Groups

> The parameter groups, allowing to define under which circumstances this validation is to be triggered
>
> ```java
> interface OnCreate {}
> interface OnUpdate {}
> ```
>
> ```java
> class InputWithGroups {
>
>    @Null(groups = OnCreate.class)
>    @NotNull(groups = OnUpdate.class)
>    private Long id;
>
>    // ...
>
>  }
> ```
>
> Using validation groups can easily become an anti-pattern since we're mixing concerns. With validation groups the validated entity has to know the validation rules for all the use cases (groups) it is used in

- [Custom Validators](https://reflectoring.io/bean-validation-with-spring-boot/#implementing-a-custom-validator)

## The 5 laws of API dates and times

> 1. Use ISO-8601 for your dates (yyyy-MM-ddTHH:mm:ssX)
> 2. Accept any timezone
> 3. Store it in UTC
> 4. Return it in UTC
>
> UTC will allow your API consumers the freedom to offset the date to whatever suits their needs.
>
> API will not have to calculate offsets for every hit
>
> 5. Don’t use time if you don’t need it
>
> While it seems like no harm done in just storing 11:59pm, or some other random time, this can get messy when it comes to internationalizing that date.

- [5 Laws for Date and Times in REST APIs](http://apiux.com/2013/03/20/5-laws-api-dates-and-times/)

## Multiple Databases

- [Multiple Databases (Baeldung)](https://www.baeldung.com/spring-data-jpa-multiple-databases)

## Axon Framework

- [Multiple Databases with Axon](https://groups.google.com/forum/#!topic/axonframework/jXjfO_DNpoU)
- [Multiple Databases with Axon (||)](https://stackoverflow.com/a/61885471)
- [Entity Scan for Axon 4.x.x](https://groups.google.com/forum/#!topic/axonframework/ZZvbIugSfko)
- [Saga Example](https://github.com/AxonFramework/Axon-trader/blob/master/orders/src/main/java/org/axonframework/samples/trader/orders/command/SellTradeManagerSaga.java)
- [Liquibase Changeset for Axon](https://github.com/bilak/axon-poc/blob/master/without-event-sourcing/src/main/resources/db/changelog/db-changelog-master.xml#L6-L121)

  > Axon automatically sets and increases the aggregate's @AggregateVersion field. There's no interaction with this field required: Just add it to your aggregate and you're done.
  > [@AggregateVersion docs](https://github.com/AxonFramework/AxonFramework/issues/721)

- [Running Axon Server in Docker](https://axoniq.io/blog-overview/running-axon-server-in-docker#0)

### Other Axon Useful Resources

- [Saga Configuration and Run Problem](https://groups.google.com/forum/#!topic/axonframework/EaHY4PgGQo8)
- [GenericJPARepository and EventStore at the same time](https://groups.google.com/forum/#!topic/axonframework/IX560dZx77U) (uses a serializer and ContainerManagedEntityManagerProvider)
- [Axon JpaEventStore with PostgreSQL configuration](https://groups.google.com/forum/#!topic/axonframework/R-QoZMlj2mM)
- [Exploring CQRS with Axon Framework](https://www.geekabyte.io/2015/10/exploring-cqrs-with-axon-framework_13.html)
- [Axon by Example](https://sgitario.github.io/axon-by-example/)

## GraphQL / Apollo Federation

- [Apollo Federation with Spring](https://github.com/apollographql/federation-jvm/tree/master/spring-example)
- [Create a Single Microservices Endpoint With GraphQL, Kotlin, and Micronaut ](https://dzone.com/articles/how-to-graphql-in-kotlin-and-micronaut)

## POSTGIS

> 4.2.2. When to use Geography Data type over Geometry data type
>
> If your data is contained in a small area, you might find that choosing an appropriate projection and using GEOMETRY is the best solution, in terms of performance and functionality available.
>
> If your data is global or covers a continental region, you may find that GEOGRAPHY allows you to build a system without having to worry about projection details. You store your data in longitude/latitude, and use the functions that have been defined on GEOGRAPHY.
>
> If you don't understand projections, and you don't want to learn about them, and you're prepared to accept the limitations in functionality available in GEOGRAPHY, then it might be easier for you to use GEOGRAPHY than GEOMETRY. Simply load your data up as longitude/latitude and go from there.

- [4.2.2. When to use Geography Data type over Geometry data type](https://postgis.net/docs/manual-2.1/using_postgis_dbmanagement.html#PostGIS_GeographyVSGeometry)

  I went with option number 3 since learning about projections wasn't my main focus when working on this side-project.

## RabbitMQ

- [When to use RabbitMQ or ApacheKafka](https://www.cloudamqp.com/blog/2019-12-12-when-to-use-rabbitmq-or-apache-kafka.html)

## Test Containers

- [Test Containers in Kotlin](https://rieckpil.de/testing-spring-boot-applications-with-kotlin-and-testcontainers/)

## F*****g Spring

- [Spring Security with JWT](https://www.toptal.com/spring/spring-security-tutorial)

## Protocol Buffers

- [Protocol Buffers (Kotlin)](https://developers.google.com/protocol-buffers/docs/reference/kotlin-generated)