package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.utils.TestParameters
import com.wks.servicemarketplace.serviceproviderservice.utils.random
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.random.Random

internal class DefaultAddressDaoIT {

    private lateinit var dataSource: DataSource
    private lateinit var addressDao: AddressDao

    companion object {
        private const val companyName = "Example Inc."
        private const val companyLogoUrl = "http://example.com/logo.jpg"
        private val companyId = CompanyId(Random.nextLong())
        private val companyUuid = CompanyUUID.random()
        private val companyPhone = PhoneNumber.random()
        private val companyEmail = Email.random()
        private val company = Company(
                0L,
                companyId,
                companyUuid,
                companyName,
                companyPhone,
                companyEmail,
                companyLogoUrl,
                createdBy = "admin"
        )

        private const val addressName = "Home"
        private const val addressLine1 = "Number 4, Privet Drive"
        private const val addressLine2 = "Magnolia Crescent"
        private const val addressCity = "London"
        private const val addressCountry = "UK"
        private val addressId = AddressId(Random.nextLong())
        private val addressUuid = AddressUUID.random()
        private val addressLatitude = BigDecimal.ZERO
        private val addressLongitude = BigDecimal.ZERO
        private val address = Address(
                0L,
                addressId,
                addressUuid,
                companyId,
                addressName,
                addressLine1,
                addressLine2,
                addressCity,
                CountryCode(addressCountry),
                addressLatitude,
                addressLongitude,
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
        addressDao = DefaultAddressDao(
                dataSource = dataSource
        )

        val companyDao = DefaultCompanyDao(dataSource)
        dataSource.connection().use {
            companyDao.save(it, company)
        }
    }

    @AfterEach
    fun tearDown() {
        dataSource.connection().use {
            it.autoCommit = false

            it.prepareStatement("DELETE FROM address").execute()
            it.prepareStatement("DELETE FROM company").execute()

            it.commit()
        }
    }

    @Test
    fun `GIVEN an address, WHEN it is saved, THEN it can be retrieved`() {
        dataSource.connection().use {
            it.autoCommit = false
            addressDao.save(it, address)
            it.commit()

            val address = addressDao.findById(it, addressId)
            assertThat(address.externalId).isEqualTo(addressId)
            assertThat(address.uuid).isEqualTo(addressUuid)
            assertThat(address.name).isEqualTo(addressName)
            assertThat(address.line1).isEqualTo(addressLine1)
            assertThat(address.line2).isEqualTo(addressLine2)
            assertThat(address.city).isEqualTo(addressCity)
            assertThat(address.countryCode.toString()).isEqualTo(addressCountry)
            assertThat(address.latitude.toInt()).isEqualTo(addressLatitude.toInt())
            assertThat(address.longitude.toInt()).isEqualTo(addressLongitude.toInt())
            assertThat(address.createdBy).isEqualTo("admin")
            assertThat(address.createdDate).isNotNull
            assertThat(address.version).isNotNull
        }
    }

    @Test
    fun `GIVEN a address, WHEN it is saved, THEN it can be retrieved by company id`() {
        dataSource.connection().use {
            dataSource.connection().use {
                it.autoCommit = false
                addressDao.save(it, address)
                it.commit()

                val address = addressDao.findByCompany(it, companyId).first()
                assertThat(address.externalId).isEqualTo(addressId)
                assertThat(address.uuid).isEqualTo(addressUuid)
                assertThat(address.name).isEqualTo(addressName)
                assertThat(address.line1).isEqualTo(addressLine1)
                assertThat(address.line2).isEqualTo(addressLine2)
                assertThat(address.city).isEqualTo(addressCity)
                assertThat(address.countryCode.toString()).isEqualTo(addressCountry)
                assertThat(address.latitude.minus(addressLatitude).toInt()).isEqualTo(0)
                assertThat(address.longitude.minus(addressLongitude).toInt()).isEqualTo(0)
                assertThat(address.createdBy).isEqualTo("admin")
                assertThat(address.createdDate).isNotNull
                assertThat(address.version).isNotNull
            }
        }
    }
}