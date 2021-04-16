package com.wks.servicemarketplace.serviceproviderservice.config

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao.DataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.DatabaseException
import liquibase.resource.ClassLoaderResourceAccessor
import java.time.Clock
import java.time.OffsetDateTime

sealed class MigrationError(open val message: String?, open val cause: Throwable) {
    data class Database(override val message: String?, override val cause: Throwable) : MigrationError(message, cause)
    data class Migration(override val message: String?, override val cause: Throwable) : MigrationError(message, cause)
}

class DatabaseMigration constructor(private val dataSource: DataSource) {

    fun migrate(): Result<Unit, MigrationError> {
        return runCatching {
            val database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection()))
            val liquibase = Liquibase("liquibase/serviceProviderService.changelog.xml", ClassLoaderResourceAccessor(), database)
            liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}")
        }.mapError {
            when(it) {
                is DatabaseException -> MigrationError.Database(it.message, it)
                else -> MigrationError.Migration(it.message, it)
            }
        }
    }
}