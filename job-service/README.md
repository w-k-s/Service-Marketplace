# Service Order Service

## Database Setup

### On Local Machine

1. Connect to your PostgreSQL database and create the `service_order_query` database using the command `CREATE DATABASE service_order_query`.

    ```shell script
    $ psql
    psql (11.5)
    Type "help" for help.
    
    =# CREATE DATABASE service_order_query
    CREATE DATABASE
    ```
2. Create a `gradle.properties` file in the same directory as `build.gradle`.
3. Provide the following details in the `gradle.properties` file.

    ```
    liquibaseTaskPrefix=liquibase
    mainUrl=jdbc:postgresql://localhost:5432/service_order_query
    username=<DB username>
    password=<DB Password>
    runList=main
    ```
    **NOTE**: This project uses the liquibase gradle plugin. This plugin creates a Gradle task for each command supported by Liquibase. The `liquibaseTaskPrefix` option will tell the liquibase plugin to capitalize the task name and prefix it with the given prefix. For example, if you put `liquibaseTaskPrefix=liquibase` in `gradle.properties`, then this plugin will create tasks named `liquibaseUpdate`, `liquibaseTag`. `liquibaseTaskPrefix` is optional, but recommended to avoid gradle task name collisions.

4. At this point, you've created the database and provided the connection parameters and liquibase configurations in the `gradle.properties` file.
You can now run the `liquibaseUpdate` command to set up the database:

    ```shell script
   cd job-service
    ./gradlew liquibaseUpdate
    ```
    
    In case you did not set liquibaseTaskPrefix in the `gradle.properties` file, you would run:
    
    ```shell script
    ./gradlew update
    ```
   
   You can overwrite the parameters in the `gralde.properties` file from the command line
   
   ```shell script
    ./gradlew liquibaseUpdate -PmainUrl=jdbc:postgresql://localhost:5432/service_order_query -Pusername=john.doe -Ppassword=123456 -PrunList=main 
    ```

## Useful Resources

##### Validation Groups
> The parameter groups, allowing to define under which circumstances this validation is to be triggered  
> 
>```java
>interface OnCreate {}
>interface OnUpdate {}
>``` 
>
>```java
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

##### The 5 laws of API dates and times
>
> 1. Use ISO-8601 for your dates (yyyy-MM-ddTHH:mm:ssX)
> 2. Accept any timezone
> 3. Store it in UTC
> 4. Return it in UTC
>
>   UTC will allow your API consumers the freedom to offset the date to whatever suits their needs.
>
>   API will not have to calculate offsets for every hit
>
> 5. Don’t use time if you don’t need it
>
>   While it seems like no harm done in just storing 11:59pm, or some other random time, this can get messy when it comes to internationalizing that date.
>   
- [5 Laws for Date and Times in REST APIs](http://apiux.com/2013/03/20/5-laws-api-dates-and-times/)

- [Multiple Databases (Baeldung)](https://www.baeldung.com/spring-data-jpa-multiple-databases)
- [Multiple Databases with Axon](https://groups.google.com/forum/#!topic/axonframework/jXjfO_DNpoU)
- [Multiple Databases with Axon (||)](https://stackoverflow.com/a/61885471)
- [Entity Scan for Axon 4.x.x](https://groups.google.com/forum/#!topic/axonframework/ZZvbIugSfko)
- [Saga Example](https://github.com/AxonFramework/Axon-trader/blob/master/orders/src/main/java/org/axonframework/samples/trader/orders/command/SellTradeManagerSaga.java)
- [Liquibase Changeset for Axon](https://github.com/bilak/axon-poc/blob/master/without-event-sourcing/src/main/resources/db/changelog/db-changelog-master.xml#L6-L121)
> Axon automatically sets and increases the aggregate's @AggregateVersion field. There's no interaction with this field required: Just add it to your aggregate and you're done.
[@AggregateVersion docs](https://github.com/AxonFramework/AxonFramework/issues/721)

- [Running Axon Server in Docker](https://axoniq.io/blog-overview/running-axon-server-in-docker#0)


- [Apollo Federation with Spring](https://github.com/apollographql/federation-jvm/tree/master/spring-example)

### might be useful later:

https://groups.google.com/forum/#!topic/axonframework/EaHY4PgGQo8
https://groups.google.com/forum/#!topic/axonframework/IX560dZx77U (uses a serializer and ContainerManagedEntityManagerProvider)
https://groups.google.com/forum/#!topic/axonframework/R-QoZMlj2mM

https://www.geekabyte.io/2015/10/exploring-cqrs-with-axon-framework_13.html
https://sgitario.github.io/axon-by-example/

### TO DO
- [ ] Acknowledge messages, quality of service?
- [ ] Rename Account Service -> Customer Service
- [ ] Create Service Provider Service
- [ ] Maybe use POSTGIS in the order service? Let's not worry about that right now.