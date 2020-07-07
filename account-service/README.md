# Account Service

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
```postgresql
CREATE DATABASE account;

CREATE SEQUENCE customer_external_id;

CREATE TABLE IF NOT EXISTS customers(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE address_external_id;

CREATE TABLE IF NOT EXISTS addresses(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	customer_external_id BIGSERIAL NOT NULL REFERENCES customers(external_id) ON UPDATE cascade ON DELETE cascade,
	name VARCHAR(50) NOT NULL check (length(name) >= 2),
	line_1 VARCHAR(100) NOT NULL check (length(line_1) >= 2),
	line_2 VARCHAR(100),
	city VARCHAR(60) NOT NULL check(length(city) >= 2),
	country_code VARCHAR(2) NOT NULL check(length(country_code) = 2),
	latitude NUMERIC(9,5) NOT NULL check(latitude >= -90.0 AND latitude <= 90.0),
    longitude NUMERIC(9,5) NOT NULL check(latitude >= -180.0 AND latitude <= 180.0),
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

create or replace function audit_record()
returns trigger as $body$
  begin
    IF (TG_OP = 'UPDATE') THEN
        new.version = old.version + 1;
        new.last_modified_date = now() at time zone 'utc';
    ELSIF (TG_OP = 'INSERT') THEN
         new.created_date = now() at time zone 'utc';
    END IF;
    return new;
  end
$body$
language plpgsql;

create trigger audit_customers 
BEFORE update on customers
for each row execute procedure audit_record();

create trigger audit_addresses 
BEFORE update on addresses
for each row execute procedure audit_record();
```

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