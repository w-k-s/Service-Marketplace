package com.wks.servicemarketplace.serviceproviderservice.config

import com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao.DataSource
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