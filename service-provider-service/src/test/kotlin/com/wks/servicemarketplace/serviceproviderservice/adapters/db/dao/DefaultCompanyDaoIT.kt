package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.utils.TestParameters
import com.wks.servicemarketplace.serviceproviderservice.utils.random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import kotlin.random.Random.Default.nextLong

internal class DefaultCompanyDaoIT {

    private lateinit var dataSource: DataSource
    private lateinit var companyDao: CompanyDao

    companion object {
        private const val companyName = "Example Inc."
        private const val companyLogoUrl = "http://example.com/logo.jpg"

        private val companyId = CompanyId(nextLong())
        private val companyUuid = CompanyUUID.random()
        private val companyPhone = PhoneNumber.random()
        private val companyEmail = Email.random()
        private val createdBy = CompanyRepresentativeUUID.random()
        private val company = Company(
                0L,
                companyId,
                companyUuid,
                companyName,
                companyPhone,
                companyEmail,
                companyLogoUrl,
                Services(Service.ELECTRICAL),
                createdBy
        )
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

    @AfterEach
    fun tearDown(){
        dataSource.connection().use {
            it.autoCommit = false
            it.prepareStatement("DELETE FROM address").execute()
            it.prepareStatement("DELETE FROM company_service").execute()
            it.prepareStatement("DELETE FROM company_admin").execute()
            it.prepareStatement("DELETE FROM employee").execute()
            it.prepareStatement("DELETE FROM company").execute()
            it.commit()
        }
    }

    @Test
    @Disabled
    @Order(1)
    fun `company external id is generated`() {
        dataSource.connection().use {
            assertThat(companyDao.newCompanyId(it)).isNotNull
        }
    }

    @Test
    @Disabled
    @Order(2)
    fun `Given a company, When it is saved, Then it can be retrieved`() {

        dataSource.connection().use {
            it.autoCommit = false
            companyDao.save(it, company)
            it.commit()

            val savedCompany = companyDao.findById(it, companyId)
            assertThat(savedCompany.externalId).isEqualTo(companyId)
            assertThat(savedCompany.uuid).isEqualTo(companyUuid)
            assertThat(savedCompany.name).isEqualTo(companyName)
            assertThat(savedCompany.phone).isEqualTo(companyPhone)
            assertThat(savedCompany.email).isEqualTo(companyEmail)
            assertThat(savedCompany.logoUrl).isEqualTo(companyLogoUrl)
            assertThat(savedCompany.createdBy).isEqualTo(createdBy)
            assertThat(savedCompany.services).isEqualTo(Services(Service.ELECTRICAL))
            assertThat(savedCompany.createdDate).isNotNull
            assertThat(savedCompany.version).isNotNull
        }
    }

    @Test
    @Disabled
    fun `an employee can be set as an administrator of a company`() {
        // Given
        val representative = CompanyRepresentative(
                0L,
                CompanyRepresentativeId(nextLong()),
                CompanyRepresentativeUUID.random(),
                Name.of("John", "Example"),
                Email.random(),
                PhoneNumber.random(),
                "admin"
        )

        val employeeDao = DefaultEmployeeDao(dataSource)

        dataSource.connection().use {
            it.autoCommit = false

            companyDao.save(it, company)

            employeeDao.save(it, representative.toEmployee(companyId, "admin"))
            val count = companyDao.setAdministrator(it, company, representative)

            it.commit()

            assertThat(count).isEqualTo(1)
        }
    }
}