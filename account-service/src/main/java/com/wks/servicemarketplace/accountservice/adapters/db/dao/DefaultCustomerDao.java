package com.wks.servicemarketplace.accountservice.adapters.db.dao;

import com.wks.servicemarketplace.accountservice.adapters.db.converters.ZonedDateTimeConverter;
import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.models.Address;
import com.wks.servicemarketplace.accountservice.core.models.CountryCode;
import com.wks.servicemarketplace.accountservice.core.models.Customer;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

public class DefaultCustomerDao extends BaseDAO implements CustomerDao {

    @Inject
    public DefaultCustomerDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long newCustomerExternalId(Connection connection) {
        return create(connection).nextval(sequence("customer_external_id")).longValue();
    }

    @Override
    public void saveCustomer(Connection connection, Customer customer) throws SQLException {
        create(connection).insertInto(
                table("customers"),
                field("external_id"),
                field("uuid"),
                field("first_name"),
                field("last_name"),
                field("created_by")
        ).values(
                customer.getExternalId(),
                customer.getUuid(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getCreatedBy()
        ).execute();
    }

    @Override
    public Long newAddressExternalId(Connection connection) {
        return create(connection).nextval(sequence("address_external_id")).longValue();
    }

    @Override
    public void saveAddress(Connection connection, Address address) {
        create(connection).insertInto(
                table("addresses"),
                field("external_id"),
                field("uuid"),
                field("customer_external_id"),
                field("name"),
                field("line_1"),
                field("line_2"),
                field("city"),
                field("country_code"),
                field("latitude"),
                field("longitude"),
                field("created_by")
        ).values(
                address.getExternalId(),
                address.getUuid(),
                address.getCustomerExternalId(),
                address.getName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getCountry().getCountryCode(),
                address.getLatitude(),
                address.getLongitude(),
                address.getCreatedBy()
        ).execute();
    }

    @Override
    public Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, long customerId, long addressId) throws SQLException {
        final Address address = create(connection)
                .select()
                .from(table("addresses"))
                .where(field("external_id").eq(addressId))
                .and(field("customer_external_id").eq(customerId))
                .limit(1)
                .fetchOne(addressRecordMapper());
        return Optional.ofNullable(address);
    }

    private RecordMapper<Record, Address> addressRecordMapper() {
        return record -> new Address(
                record.get("external_id", Long.class),
                record.get("uuid", String.class),
                record.get("customer_external_id", Long.class),
                record.get("name", String.class),
                record.get("line_1", String.class),
                record.get("line_2", String.class),
                record.get("city", String.class),
                new CountryCode(record.get("country_code", String.class)),
                record.get("latitude", BigDecimal.class),
                record.get("longitude", BigDecimal.class),
                record.get("created_date", new ZonedDateTimeConverter()),
                record.get("created_by", String.class),
                record.get("last_modified_date", new ZonedDateTimeConverter()),
                record.get("last_modified_by", String.class),
                record.get("version", Long.class)
        );
    }
}
