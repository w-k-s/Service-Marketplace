package com.wks.servicemarketplace.accountservice.config.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import com.wks.servicemarketplace.accountservice.adapters.db.dao.DataSource;

import javax.inject.Inject;

public class DatabaseHealthCheck extends HealthCheck {

    private DataSource dataSource;

    @Inject
    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected Result check() throws Exception {
        try {
            if (dataSource.getConnection().isValid(60)) {
                return HealthCheck.Result.healthy();
            }
            return HealthCheck.Result.unhealthy("Timed out connecting to the database");
        } catch (Exception e) {
            return HealthCheck.Result.unhealthy(e);
        }
    }
}
