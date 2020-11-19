package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.utils.TestParameters
import com.wks.servicemarketplace.serviceproviderservice.utils.random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import kotlin.random.Random.Default.nextLong

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DefaultCompanyRepresentativeDaoTest {

    private lateinit var dataSource: DataSource
    private lateinit var companyRepresentativeDao: CompanyRepresentativeDao

    companion object {
        private val companyRepresentativeId = CompanyRepresentativeId(nextLong())
        private val companyRepresentativeUuid = CompanyRepresentativeUUID.random()
        private val companyRepresentativeName = Name.of("John", "Example")
        private val companyRepresentativeEmail = Email.random()
        private val companyRepresentativePhoneNumber = PhoneNumber.random()
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

    @Test
    @Order(1)
    fun `can save company representative`() {
        dataSource.connection().use {
            it.autoCommit = false

            val count = companyRepresentativeDao.save(it, CompanyRepresentative(
                    0L,
                    companyRepresentativeId,
                    companyRepresentativeUuid,
                    companyRepresentativeName,
                    companyRepresentativeEmail,
                    companyRepresentativePhoneNumber,
                    createdBy = "admin"
            ))

            it.commit()

            assertThat(count).isEqualTo(1)
        }
    }

    @Test
    @Order(2)
    fun `saved company representative can be retreived`() {
        dataSource.connection().use {

            val rep = companyRepresentativeDao.findById(it, companyRepresentativeId)
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
    @Order(Integer.MAX_VALUE)
    fun `saved company representative can be deleted`() {
        dataSource.connection().use {
            it.autoCommit = false

            assertThat(companyRepresentativeDao.delete(it, companyRepresentativeId)).isEqualTo(1)

            it.commit()
        }
    }
}