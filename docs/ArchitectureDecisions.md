# Architecture Decisions

Originally, this project was intended to be an opporunity to try out the following technologies:

1. A J2EE framework other than Spring (e.g. Jersey)
1. JOOQ
1. Keycloak
1. Microservices communicating via GraphQL
1. Apache Kafka
1. Axon Framework

Of the technologies listed above, only Jersey and JOOQ remain in this project. Details about these decisions are described below:

## 1. A J2EE framework other than Spring (e.g. Jersey)

I am not a fan of the Spring Web / Boot framework.

What I dislike about it is it's magic. Put this dependency in and _snap_ it SHOULD just works.

The problem is when it doesn't work and you don't get useful error messages. When this happens, You don't know which part of your code is causing the problem and you don't know what exactly to ask (so Stackoverflow closes your question).

Debugging such problems requires experience and intuition rather than reasoning and deduction.

I prefer to have things as explicit as possible rather than annotation and reflection magic. When things go wrong, I want to be able to see where they went wrong in MY project rather than the huge, mostly irrelevant stacktrace that one gets from Spring. You must commonly experience this when you have a complex Spring Security setup which I've seen in many of the projects that I've worked on.

Furthermore, I particularly dislike Aspect-Oriented Programming:
Aspect-Oriented Programming makes it difficult to know how and when a method is called or if it is even called at all.

The worst offender is the `@Transactional` annotation. One might reasonably assume that annotating a method with `@Transactional` causes the method to execute within a transaction. However, this is only true if the `@Transactional` method is invoked from a bean.

---

I went with Jersey because it is a minimal layer above using Servlets.

For the most part, I liked working with it. I must admin that when the time came to write the code for the scheduler, it was way more boilerplate code than simply `@EnableScheduling` and annotating a method with `@Scheduler` but I am happy to write a little extra boiler plate than to have aspect oriented programming.

I would have preferred to have used constructor-based Dependency Injection in this project but I couldn't do so consistently so I went all in with HK2.

My main problem with Jersey is that it was tricky to find a good 'Getting Started' tutorial. I had to piece together bits and pieces from various blogs and StackOverflow answers. Once you get the hang of things, the [User Guide](https://eclipse-ee4j.github.io/jersey.github.io/documentation/3.0.0/index.html) is excellent.

I probably wouldn't use Jersey in a production project though, much as I hate to admit it. Even though I feel that one _can_ find the information that they're looking for when they face a problem, it's not easy and developers who are not as perhaps adventurous as I am might not looking up how to get things working on a web framework that's considered to be a bit dated (I think). I would probably go with Ktor because I definitely would still avoid Spring.

## 2. JOOQ

I'm not a fan of ORM's either; JPA in particular!
I particularly never understood nor could be bothered to learn what this whole `CascadeType` stuff is all about! `OneToOne` and `OneToMany` have always been confusing annotations that I constantly have to look up.

Personally, I find SQL pretty straight-forward and all the other ORM stuff as things that just get in the way from letting me write the query that I know I need to write.

JOOQ is brilliant! It's close to SQL syntax so its intuitive and you have an idea of what you need to write. At the same time, it allows you to build complex queries in a readable and typesafe way.

## Keycloak

If I had heard of FusionAuth before I started this project, I would have never considered Keycloak.

The documentation is so difficult to navigate! The documentation mentions an endpoint (e.g. `/user/create` or whatever), but the Base URL is at the top of the page and even if you concatenate them together - you still get 404! Where the heck am I supposed to send requests to!!

FusionAuth's documentation, UI, Client libraries are all brilliant and it's a pleasure to work with.

## Usage of RabbitMQ over Apache Kafka

- The decisions was largely based on this [blogpost](https://www.cloudamqp.com/blog/2019-12-12-when-to-use-rabbitmq-or-apache-kafka.html)

Specific Reasons:

- RabbitMQ supports AMQP which is a standard protocol so there's a freedom to choose from a variety of cloud providers. I wanted to use the cheapest one.
- RabbitMQ is easier to work with. It's documentation and tutorials are excellent. It's easy to use for the use-cases I had in mind for this little project.
- You can just get started with RabbitMQ. There's a lot more boilerplate of setup that's needed for Kafka that I simply wasn't interested in.

## GraphQL

## Axon

Axon was one of the first tools that I decided I would use in this project. I'm really glad that I only added it in one service and didn't add it in the others.

Axon is in all-or-nothing framework. You can't just use the parts that you need. This means that it's very difficult to add Axon to an existing project.

The documentation isn't great. It's very text-based. I would have preferred more examples.

Also, it's easy to use if you use the Axon-server. If you want something more custom (e.g. PostgreSQL, Kafka), the documentation isn't that helpful (and Spring JPA makes it all the more so\*\*)

_Spring JPA makes it so easy to connect your web application to one SQL database. Try to set Spring JPA with 2 databases! What a nightmare. I tried to that with Axon and it took me two days to maybe get it right. Using JOOQ - it would have been a piece of cake._
