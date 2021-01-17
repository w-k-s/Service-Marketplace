package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import java.sql.Connection
import javax.inject.Inject

class DefaultCompanyRepresentativeDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), CompanyRepresentativeDao {

    override fun newCompanyRepresentativeId(connection: Connection): CompanyRepresentativeId {
        return CompanyRepresentativeId(
                create(connection).nextval(sequence(name("company_representative_external_id"), Long::class.java))
        )
    }

    override fun save(connection: Connection, admin: CompanyRepresentative): Int {
        return create(connection).insertInto(
                table("company_representative"),
                field("external_id"),
                field("uuid"),
                field("first_name"),
                field("last_name"),
                field("email"),
                field("phone"),
                field("created_by"),
                field("last_modified_by"),
                field("version"),
        ).values(
                admin.externalId.value,
                admin.uuid.value,
                admin.name.firstName,
                admin.name.lastName,
                admin.email.value,
                admin.phone.value,
                admin.createdBy,
                admin.lastModifiedBy,
                admin.version
        ).execute()
    }

    override fun delete(connection: Connection, adminId: CompanyRepresentativeId): Int {
        return create(connection).deleteFrom(table("company_representative"))
                .where(field("external_id").eq(adminId.value))
                .execute()
    }

    override fun findByUUID(connection: Connection, id: CompanyRepresentativeUUID): CompanyRepresentative? {
        return create(connection)
                .select(
                        field("p.id"),
                        field("p.external_id"),
                        field("p.uuid"),
                        field("p.first_name"),
                        field("p.last_name"),
                        field("p.email"),
                        field("p.phone"),
                        field("p.created_date"),
                        field("p.created_by"),
                        field("p.last_modified_date"),
                        field("p.last_modified_by"),
                        field("p.version")
                )
                .from(table("company_representative").`as`("p"))
                .where(field("p.uuid").eq(id.value.toString()))
                .fetchOne()
                ?.let {
                    CompanyRepresentative(
                            it.get("p.id", Long::class.java),
                            CompanyRepresentativeId(it.get("p.external_id", Long::class.java)),
                            CompanyRepresentativeUUID.fromString(it.get("p.uuid", String::class.java)),
                            Name.of(
                                    it.get("p.first_name", String::class.java),
                                    it.get("p.last_name", String::class.java)
                            ),
                            Email.of(it.get("p.email", String::class.java)),
                            PhoneNumber.of(it.get("p.phone", String::class.java)),
                            it.get("p.created_by", String::class.java),
                            it.get("p.created_date", OffsetDateTimeConverter()),
                            it.get("p.last_modified_by", String::class.java),
                            it.get("p.last_modified_date", OffsetDateTimeConverter()),
                            it.get("p.version", Long::class.java),
                    )
                }
    }
}