package com.wks.servicemarketplace.serviceproviderservice.core

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.events.EventEnvelope
import com.wks.servicemarketplace.common.messaging.Message
import com.wks.servicemarketplace.common.messaging.MessageId
import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

interface Dao {
    @Throws(SQLException::class)
    fun connection(): Connection
}

interface CompanyDao : Dao {
    fun newCompanyId(connection: Connection): CompanyId
    fun save(connection: Connection, company: Company)
    fun findById(connection: Connection, companyId: CompanyId): Company?
    fun findByEmployeeId(connection: Connection, userId: UserId): Company?
    fun setAdministrator(connection: Connection, company: Company, admin: CompanyRepresentative): Int
}

interface EmployeeDao : Dao {
    fun save(connection: Connection, employee: Employee)
    fun findById(connection: Connection, id: EmployeeId): Employee
    fun findByCompany(connection: Connection, id: CompanyId): List<Employee>
}

interface CompanyRepresentativeDao : Dao {
    fun newCompanyRepresentativeId(connection: Connection): CompanyRepresentativeId
    fun save(connection: Connection, admin: CompanyRepresentative): Int
    fun delete(connection: Connection, adminId: CompanyRepresentativeId): Int
    fun findByUUID(connection: Connection, id: CompanyRepresentativeUUID): CompanyRepresentative?
}

interface AddressDao : Dao {
    fun save(connection: Connection, address: Address)
    fun findById(connection: Connection, id: AddressId): Address
    fun findByCompany(connection: Connection, id: CompanyId): List<Address>
}

interface EventDao : Dao {
    fun saveEvent(connection: Connection, event: EventEnvelope): Boolean
}

interface OutboxDao : Dao {
    fun saveMessage(connection: Connection, message: Message): Boolean
    fun fetchUnpublishedMessages(connection: Connection): List<Message>
    fun setMessagePublished(connection: Connection, messageId: MessageId): Boolean
}

