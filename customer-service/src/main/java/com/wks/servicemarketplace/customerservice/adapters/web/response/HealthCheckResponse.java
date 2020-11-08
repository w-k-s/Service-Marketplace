package com.wks.servicemarketplace.customerservice.adapters.web.response;

import com.google.common.base.Preconditions;

import java.util.List;

public class HealthCheckResponse {

    private final List<String> failedHealthCheck;

    private HealthCheckResponse(List<String> failedHealthChecks) {
        Preconditions.checkNotNull(failedHealthChecks);
        Preconditions.checkArgument(!failedHealthChecks.isEmpty());

        this.failedHealthCheck = failedHealthChecks;
    }

    public static HealthCheckResponse of(List<String> failedHealthChecks) {
        return new HealthCheckResponse(failedHealthChecks);
    }

    public List<String> getFailedHealthCheck() {
        return failedHealthCheck;
    }
}
