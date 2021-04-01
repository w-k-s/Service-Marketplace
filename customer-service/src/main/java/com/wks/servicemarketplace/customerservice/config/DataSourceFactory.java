package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.customerservice.adapters.db.dao.DataSource;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DataSourceFactory implements Factory<DataSource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    private DataSource dataSource;

    @Inject
    public DataSourceFactory(ApplicationParameters applicationParameters) {
        this.dataSource = new DataSource(
                applicationParameters.jdbcUrl(),
                applicationParameters.jdbcUsername(),
                applicationParameters.jdbcPassword()
        );
    }

    @Override
    public DataSource provide() {
        return dataSource;
    }

    @Override
    public void dispose(DataSource instance) {
        if (instance != null) {
            LOGGER.info("Disposing datasource");
            instance.close();
        }
    }
}
