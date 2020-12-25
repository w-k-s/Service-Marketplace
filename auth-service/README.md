# Auth Service

## FusionAuth Setup

FusionAuth v1.19.8

1. Create Tenant named `ServiceMarketplace`. Set General > Issuer to `ServiceMarketplace`.
1. Create Application `ServiceMarketplace` in Tenant `ServiceMarketplace`. Add all roles listed below.
1. Create Groups in tenant `ServiceMarketplace` and organise roles as tabulated below.
1. Enable JWT in Application `ServiceMarketplace`. Should automatically generate RSA key with SHA256, Length: 2048, Issuer: `ServiceMarketplace` (otherwise create and assign in Settings > Key Master)
1. Create API Key in tenant `ServiceMarketplace` with following roles:

    - `GET /api/group`
    - `POST /api/group/member`
    - `POST /api/login`
    - `POST /api/user/registration`

### Roles & Permissions

| Role           | Customer                                                                          | CompanyRepresentative                                 | ServiceProvider    | Admin              |
|----------------|-----------------------------------------------------------------------------------|-------------------------------------------------------|--------------------|--------------------|
| order.create   | :heavy_check_mark:                                                                |                                                       |                    | :heavy_check_mark: |
| order.delete   | :heavy_check_mark:                                                                |                                                       |                    | :heavy_check_mark: |
| order.edit     | :heavy_check_mark:                                                                |                                                       |                    | :heavy_check_mark: |
| order.view     | :heavy_check_mark:<br/> **ABAC: Only own order**                                  | :heavy_check_mark:                                    | :heavy_check_mark: | :heavy_check_mark: |
| bid.create     |                                                                                   |                                                       | :heavy_check_mark: | :heavy_check_mark: |
| bid.delete     |                                                                                   |                                                       | :heavy_check_mark: | :heavy_check_mark: |
| bid.view       | :heavy_check_mark: <br/> **ABAC: Only bids they've received**                     |                                                       | :heavy_check_mark: | :heavy_check_mark: |
| bid.edit       |                                                                                   |                                                       | :heavy_check_mark: | :heavy_check_mark: |
| bid.accept     | :heavy_check_mark: <br/> **ABAC: Only bids they've received**                     |                                                       |                    | :heavy_check_mark: |
| customer.create| :heavy_check_mark:                                                                |                                                       |                    | :heavy_check_mark: |
| customer.delete| :heavy_check_mark: <br/>   **ABAC: Only own account**                             |                                                       |                    | :heavy_check_mark: |
| customer.edit  | :heavy_check_mark: <br/>   **ABAC: Only own account**                             |                                                       |                    | :heavy_check_mark: |
| customer.view  | :heavy_check_mark: <br/>   **ABAC: Only own account + service providers account** | :heavy_check_mark:                                    | :heavy_check_mark: | :heavy_check_mark: |
| company.create |                                                                                   | :heavy_check_mark:                                    | :heavy_check_mark: | :heavy_check_mark: |
| comprep.create |                                                                                   | :heavy_check_mark:                                    |                    | :heavy_check_mark: |
| address.create | :heavy_check_mark:<br/>   **ABAC: Only own address**                              | :heavy_check_mark: <br/>  **ABAC: Only own address**  | :heavy_check_mark: | :heavy_check_mark: |
| address.delete | :heavy_check_mark:<br/>   **ABAC: Only own address**                              | :heavy_check_mark: <br/>  **ABAC: Only own address**  | :heavy_check_mark: | :heavy_check_mark: |
| address.edit   | :heavy_check_mark: <br/>   **ABAC: Only own address**                             | :heavy_check_mark: <br/>  **ABAC: Only own address**  | :heavy_check_mark: | :heavy_check_mark: |
| address.view   | :heavy_check_mark: <br/>   **ABAC: Only own address**                             | :heavy_check_mark: <br/>   **ABAC: Only own address** | :heavy_check_mark: | :heavy_check_mark: |

## Generating Key Pairs

```shell script
#!/usr/bin/env bash

openssl genrsa -out private_key.pem 4096
openssl rsa -pubout -in private_key.pem -out public_key.pem

# convert private key to pkcs8 format in order to import it from Java
openssl pkcs8 -topk8 -in private_key.pem -inform pem -out private_key_pkcs8.pem -outform pem -nocrypt
```

### ClientIds 

Generate users with the following usernames for the following. Users with usernames and no emails represent clientIds since fusionAuth does not have native support for the client_credentials grant type.
1. `auth-service`
1. `customer-service`
1. `service-provider-service`
1. `order-service`

## Environment Variables

| Environmental Variable | Description                                 | Example                                     | required |
|------------------------|---------------------------------------------|---------------------------------------------|----------|
| fusionServerUrl        | Base URL of FusionAuth server               | http://localhost:9011                       | true     |
| fusionApplicationId    | Service Marketplace Application Id          | d64656ea-4f62-4127-b312-91afeeca96f9        | true     |
| fusionTenantId         | Service Marketplace Tenant Id               | a84b174a-965c-44fe-807d-623efc3bff9c        | true     |
| fusionApiKey           | Login/Register API Key                      | 9Am1DMurFnQo6B_Zae3qLdSqd2mOk7w4APyPoCTnLHw | true     |
| jdbcUrl                | JDBC URL of service database                | jdbc:postgresql://localhost:5432/auth       | true     |
| jdbcUsername           | service database username                   |                                             | true     |
| jdbcPassword           | service database password                   |                                             | true     |
| amqpHost               | RabbitMQ Broker Host                        | localhost                                   | true     |
| amqpPort               | RabbitMQ Broker port                        | 5672                                        | true     |
| amqpPrefetchCount      | Number of unacknowledged messages processed at a given time| 10                           | false    |                                        | true     |
| clientId               | Username of auth-service user on FusionAuth | auth-service                                | true     |
| clientSecret           | Password of auth-service user on FusionAuth | password                                    | true     |
| serverHost             | Listen Address                              | http://localhost                            | true     |
| serverPort             | Listen Port                                 | 8082                                        | true     |
| retryAssignGroupIntervalMinutes | In case assign fusionAuth group fails, retry after x minutes | 5                 | false    |

**TODO**: Private key should not come from environment variables. Try Hashicorp Vault.

## CURL Commands

**Login**
```shell script
curl -X POST -H 'Content-Type: application/json' --data-raw '{"query": "mutation { signIn(data: {username: \"example@mail.com\", password: \"ThisIsMy1P@ssword\"}) { accessToken } }"}' http://localhost:8082/graphql
```

**Register**
```shell script
curl --location --request POST 'http://localhost:8082/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{
	"query": "mutation { signUp(data: {firstName: \"Jack\",lastName: \"Torrence\",email: \"jack.torrence+2@theoverlook.com\", password: \"ThisIsMy1P@ssword\",userType: CUSTOMER}) { id } }"
}'
```