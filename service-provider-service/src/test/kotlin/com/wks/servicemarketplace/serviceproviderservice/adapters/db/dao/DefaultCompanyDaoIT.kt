package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.utils.TestParameters
import com.wks.servicemarketplace.serviceproviderservice.utils.random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.random.Random.Default.nextLong

internal class DefaultCompanyDaoIT {

    private lateinit var dataSource: DataSource
    private lateinit var companyDao: CompanyDao

    companion object {
        private val companyId = CompanyId(nextLong())
        private val companyUuid = CompanyUUID.random()
        private val companyName = "Example Inc."
        private val companyPhone = PhoneNumber.random()
        private val companyEmail = Email.random()
        private val companyLogoUrl = "http://example.com/logo.jpg"
    }

    @BeforeEach
    fun setup() {
        val testParameters = TestParameters()
        dataSource = DataSource(
                jdbcUrl = testParameters.jdbcUrl,
                username = testParameters.jdbcUsername,
                password = testParameters.jdbcPassword
        )
        companyDao = DefaultCompanyDao(
                dataSource = dataSource
        )
    }

    @Test
    fun `company external id is generated`() {
        dataSource.connection().use {
            assertThat(companyDao.newCompanyId(it)).isNotNull
        }
    }

    @Test
    fun `Company can be saved`() {

        dataSource.connection().use {

            // When
            it.autoCommit = false
            companyDao.save(it, Company(
                    0L,
                    companyId,
                    companyUuid,
                    companyName,
                    companyPhone,
                    companyEmail,
                    companyLogoUrl,
                    createdBy = "admin"
            ))
            it.commit()
        }
    }

    @Test
    fun `Given a company is saved, then it can be retrieved`(){
        dataSource.connection().use {

            val company = companyDao.findById(it, companyId)
            assertThat(company.externalId).isEqualTo(companyId)
            assertThat(company.uuid).isEqualTo(companyUuid)
            assertThat(company.name).isEqualTo(companyName)
            assertThat(company.phone).isEqualTo(companyPhone)
            assertThat(company.email).isEqualTo(companyEmail)
            assertThat(company.logoUrl).isEqualTo(companyLogoUrl)
            assertThat(company.createdBy).isEqualTo("admin")
            assertThat(company.createdDate).isNotNull
            assertThat(company.version).isNotNull
        }
    }
}