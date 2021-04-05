package com.wks.servicemarketplace.customerservice.config;

import com.hubspot.algebra.Result;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DataSource;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.inject.Inject;
import java.sql.SQLException;

public class DatabaseMigration {

    private final DataSource dataSource;

    @Inject
    public DatabaseMigration(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void migrate() {
        try {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            var liquibase = new Liquibase("liquibase/customerService.changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}");
        } catch (Exception e) {
            throw new RuntimeException("Migration Failed", e);
        }
    }
}
