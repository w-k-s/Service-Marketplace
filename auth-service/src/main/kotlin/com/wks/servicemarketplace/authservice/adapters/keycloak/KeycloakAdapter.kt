package com.wks.servicemarketplace.authservice.adapters.keycloak

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.core.*
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets


class KeycloakAdapter : IAMAdapter {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KeycloakAdapter::class.java)
    }

    private val realmResource: RealmResource
    private val objectMapper: ObjectMapper
    private val customerRoleRepresentation: RoleRepresentation
    private val serviceProviderRoleRepresentation: RoleRepresentation
    private val clientId: String

    init {
        val keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8180/auth")
                .realm("ServiceMarketplace")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("254461e0-a74b-4756-8ab0-4a8e941c0f09")
                .build()

        realmResource = keycloak.realm("ServiceMarketplace")
        objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // `admin-cli` must have client role of `view-clients` in realm: `realm-management`.
        // 1. Switch to realm ServiceMarketplace
        // 2. Select Clients
        // 3. Select `admin-cli`
        // 4. Select `Service Accunt Roles`
        // 5. From Client Roles, select realm `realm-management`
        // 6. Select role `view-clients`
        val clientRepresentation = realmResource.clients().findByClientId("service-marketplace-app").first()
        val rolesResource = realmResource.clients().get(clientRepresentation.id).roles()
        customerRoleRepresentation = rolesResource[UserType.CUSTOMER.code].toRepresentation()
        serviceProviderRoleRepresentation = rolesResource[UserType.SERVICE_PROVIDER.code].toRepresentation()
        clientId = clientRepresentation.id
    }

    override fun login(credentials: Credentials): KeycloakToken {
        val uri = URIBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(8180)
                .setPath("/auth/realms/ServiceMarketplace/protocol/openid-connect/token")
                .build()

        val client = HttpClients.createDefault()
        val httpPost = HttpPost(uri)

        httpPost.entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("grant_type","password"),
                BasicNameValuePair("client_id", "service-marketplace-app"),
                BasicNameValuePair("client_secret", "a5505bca-746d-4b4d-a82d-755bcda7efa8"),
                BasicNameValuePair("username", credentials.username),
                BasicNameValuePair("password", credentials.password)
        ))

        val response = client.execute(httpPost)
        val responseBody = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
        return objectMapper.readValue(responseBody, KeycloakToken::class.java)
    }

    override fun register(identity: Registration) : Identity{
        val user = UserRepresentation()
        user.isEnabled = true
        user.username = identity.email
        user.email = identity.email
        user.firstName = identity.firstName
        user.lastName = identity.lastName

        // `admin-cli` must have client role of `manage-users` in realm: `realm-management` to create user.
        // 1. Switch to realm ServiceMarketplace
        // 2. Select Clients
        // 3. Select `admin-cli`
        // 4. Select `Service Account Roles`
        // 5. From Client Roles, select realm `realm-management`
        // 6. Select role `manage-users`
        val response = realmResource.users().create(user)
        LOGGER.info("Create User ${identity.email}. Status: ${response.status} - ${response.statusInfo}")

        val userId = CreatedResponseUtil.getCreatedId(response)

        val passwordCred = CredentialRepresentation()
        passwordCred.isTemporary = false
        passwordCred.type = CredentialRepresentation.PASSWORD
        passwordCred.value = identity.password

        val newUser = realmResource.users().get(userId)
        newUser.resetPassword(passwordCred)
        LOGGER.info("Password Reset For User ${identity.email}")

        newUser.roles().clientLevel(clientId).add(listOf(when (identity.userType) {
            UserType.SERVICE_PROVIDER -> serviceProviderRoleRepresentation
            UserType.CUSTOMER -> customerRoleRepresentation
        }))
        LOGGER.info("Role '${identity.userType.code}' set For User ${identity.email}")

        return KeycloakIdentity(userId)
    }
}