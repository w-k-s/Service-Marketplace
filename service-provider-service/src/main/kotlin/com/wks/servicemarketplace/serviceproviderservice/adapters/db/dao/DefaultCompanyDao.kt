package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import java.sql.Connection

class DefaultCompanyDao constructor(dataSource: DataSource) : BaseDao(dataSource), CompanyDao {

    override fun newCompanyId(connection: Connection): CompanyId {
        return CompanyId(
                create(connection)
                        .nextval(sequence(name("company_id"), Long::class.java))
        )
    }

    override fun save(connection: Connection, company: Company) {
        create(connection).insertInto(
                table("company"),
                field("id"),
                field("uuid"),
                field("name"),
                field("email"),
                field("phone"),
                field("logo_url"),
                field("created_by"),
                field("last_modified_by"),
                field("version"),
        ).values(
                company.id.value,
                company.uuid.value,
                company.name,
                company.email.value,
                company.phone.value,
                company.logoUrl,
                company.createdBy.value,
                company.lastModifiedBy,
                company.version
        ).execute()

        company.services.map {
            create(connection).insertInto(
                    table("company_service"),
                    field("company_uuid"),
                    field("company_id"),
                    field("service_code"),
            ).values(
                    company.uuid.value,
                    company.id.value,
                    it.code
            )
        }.let {
            create(connection).batch(it).execute()
        }
    }

    override fun findById(connection: Connection, companyId: CompanyId): Company? {
        val services : Services = Services.of(create(connection)
                .select(field("service_code"))
                .from(table("company_service"))
                .where(field("company_id").eq(companyId.value))
                .fetch { record -> record.get(field("service_code", String::class.java)) }
                .toList())

        return create(connection)
                .select(
                        field("c.id"),
                        field("c.uuid"),
                        field("c.name"),
                        field("c.email"),
                        field("c.phone"),
                        field("c.logo_url"),
                        field("c.created_date"),
                        field("c.created_by"),
                        field("c.last_modified_date"),
                        field("c.last_modified_by"),
                        field("c.version")
                )
                .from(table("company").`as`("c"))
                .where(field("c.id").eq(companyId.value))
                .fetchOne()
                ?.let {
                    Company(
                            CompanyId(it.get("c.id", Long::class.java)),
                            CompanyUUID.fromString(it.get("c.uuid", String::class.java)),
                            it.get("c.name", String::class.java),
                            PhoneNumber.of(it.get("c.phone", String::class.java)),
                            Email.of(it.get("c.email", String::class.java)),
                            it.get("c.logo_url", String::class.java),
                            services,
                            CompanyRepresentativeUUID.fromString(it.get("c.created_by", String::class.java)),
                            it.get("c.created_date", OffsetDateTimeConverter()),
                            it.get("c.last_modified_date", OffsetDateTimeConverter()),
                            it.get("c.last_modified_by", String::class.java),
                            it.get("c.version", Long::class.java),
                    )
                }
    }

    override fun findByEmployeeId(connection: Connection, employeeId: UserId): Company? {
        val company = create(connection)
                .select(
                        field("c.id"),
                        field("c.uuid"),
                        field("c.name"),
                        field("c.email"),
                        field("c.phone"),
                        field("c.logo_url"),
                        field("c.created_date"),
                        field("c.created_by"),
                        field("c.last_modified_date"),
                        field("c.last_modified_by"),
                        field("c.version")
                )
                .from(table("company").`as`("c"))
                .leftJoin(table("employeee").`as`("c"))
                .on(field("e.company_id").eq(field("c.id") as Field<Long>))
                .where(field("e.id").eq(employeeId.toString()))
                .fetchOne()
                ?.let {
                    Company(
                            CompanyId(it.get("c.id", Long::class.java)),
                            CompanyUUID.fromString(it.get("c.uuid", String::class.java)),
                            it.get("c.name", String::class.java),
                            PhoneNumber.of(it.get("c.phone", String::class.java)),
                            Email.of(it.get("c.email", String::class.java)),
                            it.get("c.logo_url", String::class.java),
                            Services.of(emptyList()),
                            CompanyRepresentativeUUID.fromString(it.get("c.created_by", String::class.java)),
                            it.get("c.created_date", OffsetDateTimeConverter()),
                            it.get("c.last_modified_date", OffsetDateTimeConverter()),
                            it.get("c.last_modified_by", String::class.java),
                            it.get("c.version", Long::class.java),
                    )
                }

        return company?.let {
            Services.of(create(connection)
                    .select(field("service_code"))
                    .from(table("company_service"))
                    .where(field("company_id").eq(it.id))
                    .fetch { record -> record.get(field("service_code", String::class.java)) }
                    .toList())
        }?.let {
            company.copy(services = it)
        }
    }

    override fun setAdministrator(connection: Connection, company: Company, admin: CompanyRepresentative): Int {
        return create(connection).insertInto(
                table("company_admin"),
                field("company_uuid"),
                field("company_id"),
                field("employee_id"),
                field("employee_uuid"),
        ).values(
                company.uuid.value,
                company.id.value,
                admin.id.value,
                admin.uuid.value
        ).execute()
    }
}