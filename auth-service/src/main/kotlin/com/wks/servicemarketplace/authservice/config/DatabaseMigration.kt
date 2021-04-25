package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.OffsetDateTime
import javax.inject.Inject

class DatabaseMigration @Inject constructor(private val dataSource: DataSource) {

    private val LOGGER = LoggerFactory.getLogger(DatabaseMigration::class.java)

    fun migrate() {
        LOGGER.info("Initializing Liquibase Migrations")

        val database =
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection()))
        val liquibase = Liquibase("liquibase/authService.changelog.xml", ClassLoaderResourceAccessor(), database)
        liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}")

        LOGGER.info("Migration complete")
    }
}