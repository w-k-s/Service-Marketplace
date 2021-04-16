package com.wks.servicemarketplace.authservice.config

import com.ufoscout.properlty.Properlty
import com.ufoscout.properlty.reader.EnvironmentVariablesReader
import com.ufoscout.properlty.reader.SystemPropertiesReader

interface ApplicationParameters {
    val serverHost: String
    val serverPort: Int
    val fusionAuthConfiguration: FusionAuthConfiguration
    val authServiceBaseUrl: String
    val jdbcUrl: String
    val jdbcUsername: String
    val jdbcPassword: String
    val amqpUri: String
    val amqpPrefetchCount: Int
    val clientId: String
    val clientSecret: String
    val retryAssignGroupIntervalMinutes: Long
    val outboxIntervalMillis: Long

    companion object {
        fun load() = DefaultApplicationParameters.load()
    }
}

data class FusionAuthConfiguration(private val provider: Properlty) {
    val serverUrl: String
        get() = provider["fusionauth.server.url"]!!
    val applicationId: String
        get() = provider["fusionauth.application.id"]!!
    val tenantId: String
        get() = provider["fusionauth.tenant.id"]!!
    val apiKey: String
        get() = provider["fusionauth.api.key"]!!
}

private class DefaultApplicationParameters(private val provider: Properlty) : ApplicationParameters {
    companion object {
        fun load(): ApplicationParameters {
            val provider = Properlty.builder()
                .caseSensitive(false)
                .add("file:/etc/servicesmarketplace/application.properties", ignoreNotFound = true)
                .add("classpath:application.properties", ignoreNotFound = true)
                .add(EnvironmentVariablesReader().replace("_", "."))
                .add(SystemPropertiesReader())
                .build()
            return DefaultApplicationParameters(provider)
        }
    }

    override val serverHost: String
        get() = provider["server.host", "0.0.0.0"]

    override val serverPort: Int
        get() = provider.getInt("server.port", 8082)

    override val fusionAuthConfiguration: FusionAuthConfiguration
        get() = FusionAuthConfiguration(provider)

    override val authServiceBaseUrl: String
        get() = provider["authservice.host.url"]!!

    override val clientId: String
        get() = provider["authservice.client.id"]!!

    override val clientSecret: String
        get() = provider["authservice.client.secret"]!!

    override val jdbcUrl: String
        get() = provider["jdbc.url"]!!

    override val jdbcUsername: String
        get() = provider["jdbc.username"]!!

    override val jdbcPassword: String
        get() = provider["jdbc.password"]!!

    override val amqpUri: String
        get() = provider["amqp.uri"]!!.let {
            it.replace("\u0020","")
        }

    override val amqpPrefetchCount: Int
        get() = provider.getInt("amqp.prefetchCount", 10)

    override val outboxIntervalMillis: Long
        get() = provider.getLong("app.outbox.intervalMillis", 500)

    override val retryAssignGroupIntervalMinutes: Long
        get() = provider.getLong("app.retryAssignGroup.intervalMinutes", 5)
}