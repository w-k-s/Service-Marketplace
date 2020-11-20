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

internal class DefaultEmployeeDaoIT {

    private lateinit var dataSource: DataSource
    private lateinit var employeeDao: EmployeeDao

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

        private const val employeeFirstName = "John"
        private const val employeeLastName = "Example"
        private val employeeId = EmployeeId(Random.nextLong())
        private val employeeUUID = EmployeeUUID.random()
        private val employeeName = Name.of(employeeFirstName, employeeLastName)
        private val employeeEmail = Email.random()
        private val employeePhone = PhoneNumber.random()
        private val employee = Employee(
                0L,
                employeeId,
                employeeUUID,
                companyId,
                employeeName,
                employeeEmail,
                employeePhone,
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
        employeeDao = DefaultEmployeeDao(
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

            it.prepareStatement("DELETE FROM employee").execute()
            it.prepareStatement("DELETE FROM company").execute()

            it.commit()
        }
    }

    @Test
    fun `GIVEN an employee, WHEN it is saved, THEN it can be retrieved`() {
        dataSource.connection().use {
            it.autoCommit = false
            employeeDao.save(it, employee)
            it.commit()

            val savedEmployee = employeeDao.findById(it, employeeId)
            assertThat(savedEmployee.externalId).isEqualTo(employeeId)
            assertThat(savedEmployee.uuid).isEqualTo(employeeUUID)
            assertThat(savedEmployee.name.first).isEqualTo(employeeFirstName)
            assertThat(savedEmployee.name.last).isEqualTo(employeeLastName)
            assertThat(savedEmployee.email).isEqualTo(employeeEmail)
            assertThat(savedEmployee.phone).isEqualTo(employeePhone)
            assertThat(savedEmployee.createdBy).isEqualTo("admin")
            assertThat(savedEmployee.createdDate).isNotNull
            assertThat(savedEmployee.version).isNotNull
        }
    }

    @Test
    fun `GIVEN a address, WHEN it is saved, THEN it can be retrieved by company id`() {
        dataSource.connection().use {
            it.autoCommit = false
            employeeDao.save(it, employee)
            it.commit()

            val savedEmployee = employeeDao.findByCompany(it, companyId).first()
            assertThat(savedEmployee.externalId).isEqualTo(employeeId)
            assertThat(savedEmployee.uuid).isEqualTo(employeeUUID)
            assertThat(savedEmployee.name.first).isEqualTo(employeeFirstName)
            assertThat(savedEmployee.name.last).isEqualTo(employeeLastName)
            assertThat(savedEmployee.email).isEqualTo(employeeEmail)
            assertThat(savedEmployee.phone).isEqualTo(employeePhone)
            assertThat(savedEmployee.createdBy).isEqualTo("admin")
            assertThat(savedEmployee.createdDate).isNotNull
            assertThat(savedEmployee.version).isNotNull
        }
    }
}