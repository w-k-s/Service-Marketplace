package com.wks.servicemarketplace.customerservice.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

import java.net.URI;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"file:/etc/servicesmarketplace/application.properties",
        "classpath:application.properties",
        "system:env",
        "system:properties",
})
public interface ApplicationParameters extends Config {

    String DEFAULT_SERVER_HOST = "0.0.0.0";
    String DEFAULT_SERVER_PORT = "8080";
    String DEFAULT_OUTBOX_INTERVAL_MILLIS = "300";

    @Key("SERVER_HOST")
    @DefaultValue(DEFAULT_SERVER_HOST)
    String serverHost();

    @Key("SERVER_PORT")
    @DefaultValue(DEFAULT_SERVER_PORT)
    int serverPort();

    @Key("JDBC_URL")
    String jdbcUrl();

    @Key("JDBC_PASSWORD")
    String jdbcPassword();

    @Key("JDBC_USERNAME")
    String jdbcUsername();

    @Key("AMQP_URI")
    String amqpUri();

    @Key("AUTHSERVICE_HOST_URL")
    String authServiceBaseUrl();

    @Key("AUTHSERVICE_CLIENT_ID")
    String clientId();

    @Key("AUTHSERVICE_CLIENT_SECRET")
    String clientSecret();

    @Key("OUTBOX_INTERVAL_MILLIS")
    @DefaultValue(DEFAULT_OUTBOX_INTERVAL_MILLIS)
    Long outboxIntervalMillis();

    static ApplicationParameters load() {
        return ConfigFactory.create(ApplicationParameters.class);
    }
}
