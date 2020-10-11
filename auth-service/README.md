### Setting up Keyclock Database
```postgresql
CREATE DATABASE keycloak;
CREATE USER keycloak WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
```

> Client Credentials grant type allows us to request an admin access token by providing client-id and client-secret instead of an admin username and password. To be able to use this approach, the OAuth 2 Client application called “admin-cli” that is in the “master” Keycloak realm, needs to be set to “confidential“.
>
> To change the “admin-cli” client Access Type property from Public to Confidential, and save the settings. You will need to login to “master” Realm, switch to OAuth 2 “Clients” list, and edit the “admin-cli“. Change the Access Type property from Public to Confidential and make sure that the “Service Accounts Enabled” option is turned on. 
>
> Once you are done making the above changes, click on the Save button. The page will reload and at the top, you will see a new tab called “Credentials“. Click on the Credentials tab and copy the value of client_secret.
- [Creating new user with Keycloak](https://www.appsdeveloperblog.com/keycloak-rest-api-create-a-new-user/)


- [Login with Keycloak](https://medium.com/devops-dudes/securing-spring-boot-rest-apis-with-keycloak-1d760b2004e)

## Environment Variables

| Environmental Variable | Description                 | Example                                     | required |
|------------------------|-----------------------------|---------------------------------------------|----------|
| keycloakServerUrl      | Base URL of keycloak server | http://localhost:8180                       | true     |
| keycloakRealm          | keycloak Realm              | ServiceMarketplace                          | true     |
| keycloakAdminId        | Admin Client Id             | admin-cli                                   | true     |
| keycloakAdminSecret    | Admin Client Secret         | 254461e0-a74b-4756-8ab0-4a8e941c0f09        | true     |
| keycloakClientId       | Client Id                   | service-marketplace-app                     | true     |
| keycloakClientSecret   | Client secret               | a5505bca-746d-4b4d-a82d-755bcda7efa8        | true     |
| serverHost             | Listen Address              | http://localhost                            | true     |
| serverPort             | Listen Port                 | 8082                                        | true     |

**To View keycloakAdminSecret:**

1. Login to keycloak as `admin`

2. Switch to keycloakRealm

3. Select clients.

4. Select `admin-cli`

5. Change `Access Type` from Public to confidential.

6. Enable 'Service Accounts Enabled'

7. Save

8. Copy Client Secret from Credentials tab.

The keycloka Admin client must have client roles of `view-clients` and  `manage-users`.

1. Switch to keycloakRealm 

2. Select Clients

3. Select `admin-cli`

4. Select `Service Account Roles`

5. From Client Roles, select realm `realm-management`

6. Add role `view-clients` and `manage users`

**To View keycloakClientSecret:**

1. Login to keycloak as `admin`

2. Switch to keycloakRealm

3. Select clients.

4. Select `client` matching (keycloakClientId)

5. Change `Access Type` from Public to confidential.

6. Enable 'Service Accounts Enabled'

7. Save

8. Copy Client Secret from Credentials tab

---