package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.adapters.db.dao.DataSource;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class DataSourceFactory implements Factory<DataSource> {

    private DataSource dataSource;

    @Inject
    public DataSourceFactory(ApplicationParameters applicationParameters) {
        this.dataSource = new DataSource(
                applicationParameters.getJdbcUrl(),
                applicationParameters.getJdbcUsername(),
                applicationParameters.getJdbcPassword()
        );
    }

    @Override
    public DataSource provide() {
        return dataSource;
    }

    @Override
    public void dispose(DataSource instance) {
        if (instance != null) {
            instance.close();
        }
    }
}
