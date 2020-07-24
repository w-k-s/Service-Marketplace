package com.wks.servicemarketplace.authservice.adapters.keycloak

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.config.KeycloakConfiguration
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
import javax.inject.Inject


class KeycloakAdapter @Inject constructor(private val config: KeycloakConfiguration) : IAMAdapter {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KeycloakAdapter::class.java)
    }

    private val realmResource: RealmResource
    private val objectMapper: ObjectMapper
    private val customerRoleRepresentation: RoleRepresentation
    private val serviceProviderRoleRepresentation: RoleRepresentation
    private val clientId: String

    init {
        val authEndpoint = URIBuilder(config.serverUrl)
                .setPath("auth")
                .build()
                .toString()

        val keycloak = KeycloakBuilder.builder()
                .serverUrl(authEndpoint)
                .realm(config.realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(config.adminId)
                .clientSecret(config.adminSecret)
                .build()

        realmResource = keycloak.realm(config.realm)
        objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // `admin-cli` must have client role of `view-clients` in realm: `realm-management`.
        // 1. Switch to realm ServiceMarketplace
        // 2. Select Clients
        // 3. Select `admin-cli`
        // 4. Select `Service Accunt Roles`
        // 5. From Client Roles, select realm `realm-management`
        // 6. Select role `view-clients`
        val clientRepresentation = realmResource.clients().findByClientId(config.clientId).first()
        val rolesResource = realmResource.clients().get(clientRepresentation.id).roles()
        customerRoleRepresentation = rolesResource[UserType.CUSTOMER.code].toRepresentation()
        serviceProviderRoleRepresentation = rolesResource[UserType.SERVICE_PROVIDER.code].toRepresentation()
        clientId = clientRepresentation.id
    }

    override fun login(credentials: Credentials): KeycloakToken {
        val uri = URIBuilder(config.serverUrl)
                .setPath("/auth/realms/${config.realm}/protocol/openid-connect/token")
                .build()

        val client = HttpClients.createDefault()
        val httpPost = HttpPost(uri)

        httpPost.entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("grant_type", "password"),
                BasicNameValuePair("client_id", config.clientId),
                BasicNameValuePair("client_secret", config.clientSecret),
                BasicNameValuePair("username", credentials.username),
                BasicNameValuePair("password", credentials.password)
        ))

        val response = client.execute(httpPost)
        val responseBody = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
        return objectMapper.readValue(responseBody, KeycloakToken::class.java)
    }

    override fun register(identity: Registration): Identity {
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