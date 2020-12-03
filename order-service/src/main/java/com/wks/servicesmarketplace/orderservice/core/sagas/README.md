## Sagas

**Note: Why are sagas implemented in Java?**

Axon persists saga state in the DB.
 However, injected resources e.g. `CommandGateway` and `EventProducer` are meant to be injected and not serialized along with the saga state.
 To do this, these injected resources must be marked with `transient`.
 
 To mark a field as `transient` in Kotlin, it needs to be annotated with `@Transient`.
 After repeated tries, I could not get this to work and Saga serialization failed.
 In the end, I decided to implement the sagas in Java to get something working. 