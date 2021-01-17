package com.wks.servicemarketplace.serviceproviderservice.adapters.db.dao

import com.wks.servicemarketplace.common.AddressId
import com.wks.servicemarketplace.common.AddressUUID
import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.math.BigDecimal
import java.sql.Connection
import javax.inject.Inject

class DefaultAddressDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), AddressDao {

    private val addressMapper = RecordMapper<Record, Address> { record ->
        record?.let {
            Address(
                    it.get("a.id", Long::class.java),
                    AddressId.of(it.get("a.external_id", Long::class.java)),
                    AddressUUID.fromString(it.get("a.uuid", String::class.java)),
                    CompanyId(it.get("a.company_external_id", Long::class.java)),
                    it.get("a.name", String::class.java),
                    it.get("a.line_1", String::class.java),
                    it.get("a.line_2", String::class.java),
                    it.get("a.city", String::class.java),
                    CountryCode.of(it.get("a.country_code", String::class.java)),
                    it.get("a.latitude", BigDecimal::class.java),
                    it.get("a.longitude", BigDecimal::class.java),
                    it.get("a.created_by", String::class.java),
                    it.get("a.created_date", OffsetDateTimeConverter()),
                    it.get("a.last_modified_by", String::class.java),
                    it.get("a.last_modified_date", OffsetDateTimeConverter()),
                    it.get("a.version", Long::class.java),
            )
        }
    }

    override fun save(connection: Connection, address: Address) {
        create(connection).insertInto(
                table("address"),
                field("external_id"),
                field("uuid"),
                field("company_external_id"),
                field("name"),
                field("line_1"),
                field("line_2"),
                field("city"),
                field("country_code"),
                field("latitude"),
                field("longitude"),
                field("created_by"),
                field("last_modified_by"),
                field("version"),
        ).values(
                address.externalId.value,
                address.uuid.value,
                address.companyId.value,
                address.name,
                address.line1,
                address.line2,
                address.city,
                address.countryCode.toString(),
                address.latitude,
                address.longitude,
                address.createdBy,
                address.lastModifiedBy,
                address.version
        ).execute()
    }

    override fun findById(connection: Connection, id: AddressId): Address {
        return create(connection)
                .select(
                        field("a.id"),
                        field("a.external_id"),
                        field("a.uuid"),
                        field("a.company_external_id"),
                        field("a.name"),
                        field("a.line_1"),
                        field("a.line_2"),
                        field("a.city"),
                        field("a.country_code"),
                        field("a.latitude"),
                        field("a.longitude"),
                        field("a.created_date"),
                        field("a.created_by"),
                        field("a.last_modified_date"),
                        field("a.last_modified_by"),
                        field("a.version")
                )
                .from(table("address").`as`("a"))
                .where(field("a.external_id").eq(id.value))
                .fetchOne(addressMapper)
    }

    override fun findByCompany(connection: Connection, id: CompanyId): List<Address> {
        return create(connection)
                .select(
                        field("a.id"),
                        field("a.external_id"),
                        field("a.uuid"),
                        field("a.company_external_id"),
                        field("a.name"),
                        field("a.line_1"),
                        field("a.line_2"),
                        field("a.city"),
                        field("a.country_code"),
                        field("a.latitude"),
                        field("a.longitude"),
                        field("a.created_date"),
                        field("a.created_by"),
                        field("a.last_modified_date"),
                        field("a.last_modified_by"),
                        field("a.version")
                )
                .from(table("address").`as`("a"))
                .where(field("a.company_external_id").eq(id.value))
                .fetch(addressMapper)
    }
}