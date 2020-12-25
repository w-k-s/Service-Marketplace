package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.db.dao.DataSource
import org.glassfish.hk2.api.Factory
import javax.inject.Inject

class DataSourceFactory @Inject constructor(parameters: ApplicationParameters) : Factory<DataSource> {

    private val dataSource = DataSource(
            parameters.jdbcUrl,
            parameters.jdbcUsername,
            parameters.jdbcPassword
    )

    override fun provide() = dataSource

    override fun dispose(instance: DataSource?) { /*noop*/}
}