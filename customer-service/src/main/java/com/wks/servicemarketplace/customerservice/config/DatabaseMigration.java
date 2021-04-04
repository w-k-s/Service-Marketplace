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

    enum ErrorType{
        CONNECTION,
        SQL_SYNTAX,
        MIGRATION
    }

    static class MigrationError{
        public final ErrorType type;
        public final String message;

        private MigrationError(ErrorType type, String message){
            this.type = type;
            this.message = message;
        }

        @Override
        public String toString() {
            return "MigrationError{" +
                    "type=" + type +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    private final DataSource dataSource;

    @Inject
    public DatabaseMigration(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Result<Void, MigrationError> migrate() {
        try {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            var liquibase = new Liquibase("liquibase/customerService.changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}");
            return Result.ok(null);
        } catch (DatabaseException e) {
            return Result.err(new MigrationError(ErrorType.CONNECTION, e.getMessage()));
        } catch (SQLException e) {
            return Result.err(new MigrationError(ErrorType.SQL_SYNTAX, e.getMessage()));
        } catch (LiquibaseException e) {
            return Result.err(new MigrationError(ErrorType.MIGRATION, e.getMessage()));
        }
    }
}
