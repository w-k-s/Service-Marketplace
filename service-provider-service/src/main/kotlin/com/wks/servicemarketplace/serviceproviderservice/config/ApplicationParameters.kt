package com.wks.servicemarketplace.serviceproviderservice.config

import com.ufoscout.properlty.Properlty
import com.ufoscout.properlty.reader.EnvironmentVariablesReader
import com.ufoscout.properlty.reader.SystemPropertiesReader

interface ApplicationParameters {
    val serverHost: String
    val serverPort: Int
    val amqpUri: String
    val jdbcUrl: String
    val jdbcUsername: String
    val jdbcPassword: String
    val clientId: String
    val clientSecret: String
    val authServiceBaseUrl: String
    val outboxIntervalMillis: Long

    companion object {
        const val DEFAULT_OUTBOX_INTERVAL_MILLIS: Long = 300

        fun load() = DefaultApplicationParameters.load()
    }
}

private data class DefaultApplicationParameters(private val provider: Properlty) : ApplicationParameters{
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
        get() = provider.getInt("server.port", 8084)

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
        get() = provider["amqp.uri"]!!

    override val outboxIntervalMillis: Long
        get() = provider.getLong("app.outbox.intervalMillis", 500)

}