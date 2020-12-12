# Service Proveider Service

## Environment Variables

| Environmental Variable | Description                                 | Example                                     | required |
|------------------------|---------------------------------------------|---------------------------------------------|----------|
| jdbcUrl                | JDBC URL of service database                | jdbc:postgresql://localhost:5432/provider   | true     |
| jdbcUsername           | service database username                   |                                             | true     |
| jdbcPassword           | service database password                   |                                             | true     |
| amqpHost               | RabbitMQ Broker Host                        | localhost                                   | true     |
| amqpPort               | RabbitMQ Broker port                        | 5672                                        | true     |
| clientId               | Username of service-provider-service user on FusionAuth | service-provider-service        | true     |
| clientSecret           | Password of service-provider-service user on FusionAuth | password                        | true     |
| serverHost             | Listen Address                              | http://localhost                            | true     |
| serverPort             | Listen Port                                 | 8084                                        | true     |

**TODO**: Public key should not come from environment variables. Try Hashicorp Vault.
