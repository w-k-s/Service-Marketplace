# Auth Service

## Environment Variables

| Environmental Variable | Description                 | Example                                     | required |
|------------------------|-----------------------------|---------------------------------------------|----------|
| fusionServerUrl        | Base URL of FusionAuth server | http://localhost:9011                     | true     |
| fusionApplicationId          | Service Marketplace Application Id | d64656ea-4f62-4127-b312-91afeeca96f9                          | true     |
| fusionTenantId        | Service Marketplace Tenant Id             | a84b174a-965c-44fe-807d-623efc3bff9c                                   | true     |
| fusionApiKey    | Login/Register API Key         | 9Am1DMurFnQo6B_Zae3qLdSqd2mOk7w4APyPoCTnLHw        | true     |
| fusionCustomerGroupId       | Id of Customer Group                   | 84c432eb-4649-412e-8499-d06d7d37ad31                     | true     |
| fusionServiceProviderGroupId   | Id of ServiceProvider Group              | fb3bab1e-9388-4d63-8690-b947115ccf1a        | true     |
| serverHost             | Listen Address              | http://localhost                            | true     |
| serverPort             | Listen Port                 | 8082                                        | true     |

## CURL Commands

**Login**
```shell script
curl -X POST -H 'Content-Type: application/json' --data-raw '{"query": "mutation { signIn(data: {username: \"example@mail.com\", password: \"ThisIsMy1P@ssword\"}) { accessToken } }"}' http://localhost:8082/graphql
```

**Register**
```shell script

```