package com.wks.servicemarketplace.accountservice.config;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ApplicationParameters {

    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final String DEFAULT_PORT = "8080";

    @Builder.Default
    private final String host = DEFAULT_HOST;
    @Builder.Default
    private final String port = DEFAULT_PORT;
    @NonNull
    private final String jdbcUrl;
    @NonNull
    private final String jdbcPassword;
    @NonNull
    private final String jdbcUsername;
}
