package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao.DataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.time.Clock
import java.time.OffsetDateTime

class DatabaseMigration constructor(private val dataSource: DataSource) {

    fun migrate() {
        val database =
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection()))
        val liquibase = Liquibase("liquibase/serviceProviderService.changelog.xml", ClassLoaderResourceAccessor(), database)
        liquibase.update("Migration at ${OffsetDateTime.now(Clock.systemUTC())}")
    }
}