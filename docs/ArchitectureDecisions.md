# Architecture Decisions

## Usage of RabbitMQ over Apache Kafka

- The decisions was largely based on this [blogpost](https://www.cloudamqp.com/blog/2019-12-12-when-to-use-rabbitmq-or-apache-kafka.html)

Specific Reasons:

- RabbitMQ supports AMQP which is a standard protocol so there's a freedom to choose from a variety of cloud providers. I wanted to use the cheapest one.
- RabbitMQ is easier to work with. It's documentation and tutorials are excellent. It's easy to use for the use-cases I had in mind for this little project.
- You can just get started with RabbitMQ. There's a lot more boilerplate of setup that's needed for Kafka that I simply wasn't interested in.
