# Service Order Service

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

> Axon automatically sets and increases the aggregate's @AggregateVersion field. There's no interaction with this field required: Just add it to your aggregate and you're done.
[@AggregateVersion docs](https://github.com/AxonFramework/AxonFramework/issues/721)

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