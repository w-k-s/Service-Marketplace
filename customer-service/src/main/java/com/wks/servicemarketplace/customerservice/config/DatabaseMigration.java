package com.wks.servicemarketplace.customerservice.config;

import com.hubspot.algebra.Result;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DataSource;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.inject.Inject;

public class DatabaseMigration {

    private final DataSource dataSource;

    @Inject
    public DatabaseMigration(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Result<Void, Exception> migrate() {
        try {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            var liquibase = new Liquibase("liquibase/customerService.changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}");
            return Result.ok(null);
        } catch (Exception e) {
            return Result.err(e);
        }
    }
}
