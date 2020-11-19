package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.SQLException

data class DataSource(
        val jdbcUrl: String,
        val username: String,
        val password: String
) : AutoCloseable {

    private val dataSource: HikariDataSource = HikariConfig().also {
        it.jdbcUrl = jdbcUrl
        it.username = username
        it.password = it.password
        it.addDataSourceProperty("cachePrepStmts", "true")
        it.addDataSourceProperty("prepStmtCacheSize", "250")
        it.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    }.let { HikariDataSource(it) }

    @Throws(SQLException::class)
    fun connection() = dataSource.connection

    override fun close() {
        dataSource.use { }
    }
}