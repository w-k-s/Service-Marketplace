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