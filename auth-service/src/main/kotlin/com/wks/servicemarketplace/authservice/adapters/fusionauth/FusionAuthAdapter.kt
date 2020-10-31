package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicemarketplace.authservice.config.FusionAuthConfiguration
import com.wks.servicemarketplace.authservice.core.*
import com.wks.servicemarketplace.authservice.core.errors.DuplicateUsernameException
import com.wks.servicemarketplace.authservice.core.errors.UnauthorizedException
import com.wks.servicemarketplace.authservice.core.errors.UserNotFoundException
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class FusionAuthAdapter @Inject constructor(private val config: FusionAuthConfiguration,
                                            private val objectMapper: ObjectMapper) : IAMAdapter {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(FusionAuthAdapter::class.java)
    }

    private val client = HttpClients.createDefault()

    override fun login(credentials: Credentials): Token {
        try {
            val entity = objectMapper.writeValueAsString(FusionAuthLoginRequest(credentials, config.applicationId))

            return URIBuilder(config.serverUrl)
                    .setPath("/api/login")
                    .build()
                    .let { HttpPost(it) }
                    .also { it.addHeader("X-FusionAuth-TenantId", config.tenantId) }
                    .also { it.addHeader("Authorization", config.apiKey) }
                    .also { it.entity = StringEntity(entity, ContentType.APPLICATION_JSON) }
                    .let { client.execute(it) }
                    .also { LOGGER.info("Login: Username: {}. Status: {}", credentials.username, it.statusLine.statusCode) }
                    .also {
                        when (it.statusLine.statusCode) {
                            401 -> throw UnauthorizedException()
                            404 -> throw UserNotFoundException(credentials.username)
                        }
                    }
                    .let { EntityUtils.toString(it.entity, StandardCharsets.UTF_8) }
                    .also { LOGGER.info("Login: Username: {}. Response: {}", credentials.username, it) }
                    .let { objectMapper.readValue(it, FusionAuthLoginResponse::class.java) }

        } catch (e: Exception) {
            LOGGER.error(e.message, e)
            throw(e)
        }
    }

    override fun register(registration: Registration): Identity {
        try {
            val entity = objectMapper.writeValueAsString(FusionAuthRegistrationRequest(registration, config.applicationId))
            return URIBuilder(config.serverUrl)
                    .setPath("/api/user/registration")
                    .build()
                    .let { HttpPost(it) }
                    .also { it.addHeader("X-FusionAuth-TenantId", config.tenantId) }
                    .also { it.addHeader("Authorization", config.apiKey) }
                    .also { it.entity = StringEntity(entity, ContentType.APPLICATION_JSON) }
                    .let { client.execute(it) }
                    .also { LOGGER.info("Register: Username: {}. Status: {}", registration.username, it.statusLine.statusCode) }
                    .also {
                        when (it.statusLine.statusCode) {
                            401 -> throw UnauthorizedException()
                            400 -> throw DuplicateUsernameException(registration.username)
                        }
                    }
                    .let { EntityUtils.toString(it.entity, StandardCharsets.UTF_8) }
                    .also { LOGGER.info("Register: Username: {}. Response: {}", registration.username, it) }
                    .let { objectMapper.readValue(it, FusionAuthRegistrationResponse::class.java) }
                    .also { addUserToGroup(registration.userType, it.id) }

        } catch (e: Exception) {
            LOGGER.error(e.message, e)
            throw(e)
        }
    }

    private fun addUserToGroup(role: UserType, userId: String) {

        val entity = objectMapper.writeValueAsString(FusionAuthAddUserToGroupRequest(
                userId = userId,
                groupId = when (role) {
                    UserType.CUSTOMER -> config.customerGroupId
                    UserType.SERVICE_PROVIDER -> config.serviceProviderGroupId
                    else -> throw IllegalArgumentException("Group Id for role $role is unknown")
                }
        ))

        URIBuilder(config.serverUrl)
                .setPath("/api/group/member")
                .build()
                .let { HttpPost(it) }
                .also { it.addHeader("X-FusionAuth-TenantId", config.tenantId) }
                .also { it.addHeader("Authorization", config.apiKey) }
                .also { it.entity = StringEntity(entity, ContentType.APPLICATION_JSON) }
                .let { client.execute(it) }
                .also { LOGGER.info("Add User To Group: User Id: {}, role: {}. Status: {}", userId, role, it.statusLine.statusCode) }

    }
}