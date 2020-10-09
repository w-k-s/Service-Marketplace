package com.wks.servicemarketplace.accountservice.config.healthchecks;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.wks.servicemarketplace.accountservice.adapters.db.dao.DataSource;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class HealthChecksFactory implements Factory<HealthCheckRegistry> {

    private final HealthCheckRegistry healthChecks;

    @Inject
    public HealthChecksFactory(DataSource dataSource) {
        healthChecks = new HealthCheckRegistry();
        healthChecks.register("datasource", new DatabaseHealthCheck(dataSource));
    }

    @Override
    public HealthCheckRegistry provide() {
        return healthChecks;
    }

    @Override
    public void dispose(HealthCheckRegistry instance) {

    }
}
