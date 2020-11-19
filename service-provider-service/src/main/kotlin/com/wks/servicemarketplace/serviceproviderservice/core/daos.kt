package com.wks.servicemarketplace.serviceproviderservice.core

import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

interface Dao {
    @Throws(SQLException::class)
    fun connection(): Connection
}

interface CompanyDao {
    fun newCompanyId(connection: Connection): CompanyId
    fun save(connection: Connection, company: Company)
    fun findById(connection: Connection, companyId: CompanyId): Company
    fun setAdministrator(connection: Connection, company: Company, admin: CompanyRepresentative): Int
}

interface EmployeeDao {
    fun save(connection: Connection, employee: Employee)
    fun findById(connection: Connection, id: EmployeeId): Employee
    fun findByCompany(connection: Connection, id: CompanyId): List<Employee>
}

interface CompanyRepresentativeDao {
    fun save(connection: Connection, admin: CompanyRepresentative): Int
    fun delete(connection: Connection, adminId: CompanyRepresentativeId): Int
    fun findById(connection: Connection, id: CompanyRepresentativeId): CompanyRepresentative
}

interface AddressDao {
    fun save(connection: Connection, address: Address)
    fun findById(connection: Connection, id: AddressId): Address
    fun findByCompany(connection: Connection, id: CompanyId): List<Address>
}
