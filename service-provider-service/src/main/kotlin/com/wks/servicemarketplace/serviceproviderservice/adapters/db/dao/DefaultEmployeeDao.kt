package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.sql.Connection
import javax.inject.Inject

class DefaultEmployeeDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), EmployeeDao {

    private val employeeMapper = RecordMapper<Record, Employee> { record ->
        record?.let {
            Employee(
                    it.get("e.id", Long::class.java),
                    EmployeeId(it.get("e.external_id", Long::class.java)),
                    EmployeeUUID.fromString(it.get("e.uuid", String::class.java)),
                    CompanyId(it.get("e.company_external_id", Long::class.java)),
                    Name.of(
                            it.get("e.first_name", String::class.java),
                            it.get("e.last_name", String::class.java)
                    ),
                    Email.of(it.get("e.email", String::class.java)),
                    PhoneNumber.of(it.get("e.phone", String::class.java)),
                    it.get("e.created_by", String::class.java),
                    it.get("e.created_date", OffsetDateTimeConverter()),
                    it.get("e.last_modified_by", String::class.java),
                    it.get("e.last_modified_date", OffsetDateTimeConverter()),
                    it.get("e.version", Long::class.java),
            )
        }
    }

    override fun save(connection: Connection, employee: Employee) {
        create(connection).insertInto(
                table("employee"),
                field("external_id"),
                field("uuid"),
                field("first_name"),
                field("last_name"),
                field("email"),
                field("phone"),
                field("company_external_id"),
                field("created_by"),
                field("last_modified_by"),
                field("version"),
        ).values(
                employee.externalId.value,
                employee.uuid.value,
                employee.name.firstName,
                employee.name.lastName,
                employee.email.value,
                employee.phone.value,
                employee.companyId.value,
                employee.createdBy,
                employee.lastModifiedBy,
                employee.version
        ).execute()
    }

    override fun findById(connection: Connection, id: EmployeeId): Employee {
        return create(connection)
                .select(
                        field("e.id"),
                        field("e.external_id"),
                        field("e.uuid"),
                        field("e.first_name"),
                        field("e.last_name"),
                        field("e.email"),
                        field("e.phone"),
                        field("e.company_external_id"),
                        field("e.created_date"),
                        field("e.created_by"),
                        field("e.last_modified_date"),
                        field("e.last_modified_by"),
                        field("e.version")
                )
                .from(table("employee").`as`("e"))
                .where(field("e.external_id").eq(id.value))
                .fetchOne(employeeMapper)
    }

    override fun findByCompany(connection: Connection, id: CompanyId): List<Employee> {
        return create(connection)
                .select(
                        field("e.id"),
                        field("e.external_id"),
                        field("e.uuid"),
                        field("e.first_name"),
                        field("e.last_name"),
                        field("e.email"),
                        field("e.phone"),
                        field("e.company_external_id"),
                        field("e.created_date"),
                        field("e.created_by"),
                        field("e.last_modified_date"),
                        field("e.last_modified_by"),
                        field("e.version")
                )
                .from(table("employee").`as`("e"))
                .where(field("e.company_external_id").eq(id.value))
                .fetch(employeeMapper)
    }
}