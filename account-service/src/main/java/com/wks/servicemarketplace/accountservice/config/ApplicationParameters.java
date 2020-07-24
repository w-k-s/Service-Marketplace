package com.wks.servicemarketplace.accountservice.config;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ApplicationParameters {

    private static final String DEFAULT_SERVER_HOST = "0.0.0.0";
    private static final String DEFAULT_SERVER_PORT = "8080";

    @Builder.Default
    private final String serverHost = DEFAULT_SERVER_HOST;
    @Builder.Default
    private final String serverPort = DEFAULT_SERVER_PORT;
    @NonNull
    private final String jdbcUrl;
    @NonNull
    private final String jdbcPassword;
    @NonNull
    private final String jdbcUsername;
    @NonNull
    private final String amqpHost;
    private final int amqpPort;
    @NonNull
    private final String keycloakKeySetUrl;
}
