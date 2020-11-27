package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.jooq.impl.DSL.*
import java.sql.Connection
import javax.inject.Inject

class DefaultCompanyDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), CompanyDao {

    override fun newCompanyId(connection: Connection): CompanyId {
        return CompanyId(
                create(connection)
                        .nextval(sequence(name("company_external_id"), Long::class.java))
        )
    }

    override fun save(connection: Connection, company: Company) {
        create(connection).insertInto(
                table("company"),
                field("external_id"),
                field("uuid"),
                field("name"),
                field("email"),
                field("phone"),
                field("logo_url"),
                field("created_by"),
                field("last_modified_by"),
                field("version"),
        ).values(
                company.externalId.value,
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
                    field("company_external_id"),
                    field("service_code"),
            ).values(
                    company.uuid.value,
                    company.externalId.value,
                    it.code
            )
        }.let {
            create(connection).batch(it).execute()
        }
    }

    override fun findById(connection: Connection, companyId: CompanyId): Company {
        val services : Services = Services.of(create(connection)
                .select(field("service_code"))
                .from(table("company_service"))
                .where(field("company_external_id").eq(companyId.value))
                .fetch { record -> record.get(field("service_code", String::class.java)) }
                .toList())

        return create(connection)
                .select(
                        field("c.id"),
                        field("c.external_id"),
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
                .where(field("c.external_id").eq(companyId.value))
                .fetchOne()
                .let {
                    Company(
                            it.get("c.id", Long::class.java),
                            CompanyId(it.get("c.external_id", Long::class.java)),
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

    override fun setAdministrator(connection: Connection, company: Company, admin: CompanyRepresentative): Int {
        return create(connection).insertInto(
                table("company_admin"),
                field("company_uuid"),
                field("company_external_id"),
                field("employee_id"),
                field("employee_uuid"),
        ).values(
                company.uuid.value,
                company.externalId.value,
                admin.externalId.value,
                admin.uuid.value
        ).execute()
    }
}