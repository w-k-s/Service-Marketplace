package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.utils.TestParameters
import com.wks.servicemarketplace.serviceproviderservice.utils.random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import kotlin.random.Random.Default.nextLong

class DefaultCompanyRepresentativeDaoIT {

    private lateinit var dataSource: DataSource
    private lateinit var companyRepresentativeDao: CompanyRepresentativeDao

    companion object {
        private val companyRepresentativeId = CompanyRepresentativeId(nextLong())
        private val companyRepresentativeUuid = CompanyRepresentativeUUID.random()
        private val companyRepresentativeName = Name.of("John", "Example")
        private val companyRepresentativeEmail = Email.random()
        private val companyRepresentativePhoneNumber = PhoneNumber.random()
        private val companyRepresentative = CompanyRepresentative(
                0L,
                companyRepresentativeId,
                companyRepresentativeUuid,
                companyRepresentativeName,
                companyRepresentativeEmail,
                companyRepresentativePhoneNumber,
                "admin"
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
        companyRepresentativeDao = DefaultCompanyRepresentativeDao(
                dataSource = dataSource
        )
    }

    @AfterEach
    fun tearDown(){
        dataSource.connection().use {
            it.autoCommit = false

            it.prepareStatement("DELETE FROM company_representative").execute()

            it.commit()
        }
    }

    @Test
    fun `GIVEN a company representative, WHEN it is saved, THEN it can be retrieved`() {
        dataSource.connection().use {
            it.autoCommit = false

            val count = companyRepresentativeDao.save(it, companyRepresentative)

            it.commit()

            assertThat(count).isEqualTo(1)
            val rep = companyRepresentativeDao.findByUUID(it, companyRepresentativeUuid)!!
            assertThat(rep.externalId).isEqualTo(companyRepresentativeId)
            assertThat(rep.uuid).isEqualTo(companyRepresentativeUuid)
            assertThat(rep.name).isEqualTo(companyRepresentativeName)
            assertThat(rep.phone).isEqualTo(companyRepresentativePhoneNumber)
            assertThat(rep.email).isEqualTo(companyRepresentativeEmail)
            assertThat(rep.createdBy).isEqualTo("admin")
            assertThat(rep.createdDate).isNotNull
            assertThat(rep.version).isNotNull
        }
    }

    @Test
    fun `GIVEN a company representative, WHEN it is saved, THEN it can be deleted`() {
        dataSource.connection().use {
            it.autoCommit = false
            companyRepresentativeDao.save(it, companyRepresentative)
            it.commit()

            assertThat(companyRepresentativeDao.delete(it, companyRepresentativeId)).isEqualTo(1)

            it.commit()
        }
    }
}