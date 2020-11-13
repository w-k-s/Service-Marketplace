package com.wks.servicemarketplace.customerservice.adapters.db.dao;

import com.wks.servicemarketplace.customerservice.adapters.db.converters.OffsetDateTimeConverter;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.address.CountryCode;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import lombok.val;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

public class DefaultCustomerDao extends BaseDAO implements CustomerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerDao.class);

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
        // TODO: Save addresses
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
    public Optional<Customer> findCustomerByUuid(Connection connection, String uuid) {
        final List<Customer> customerPerAddress = create(connection)
                .select(
                        field("c.external_id"),
                        field("c.uuid"),
                        field("c.first_name"),
                        field("c.last_name"),
                        field("c.created_date"),
                        field("c.created_by"),
                        field("c.last_modified_date"),
                        field("c.last_modified_by"),
                        field("c.version"),
                        field("a.external_id"),
                        field("a.uuid"),
                        field("a.name"),
                        field("a.line_1"),
                        field("a.line_2"),
                        field("a.city"),
                        field("a.country_code"),
                        field("a.latitude"),
                        field("a.longitude"),
                        field("a.created_by"),
                        field("a.created_date"),
                        field("a.last_modified_date"),
                        field("a.last_modified_by"),
                        field("a.version")
                )
                .from(table("customers").as("c"))
                .leftJoin(table("addresses").as("a"))
                .on(field("c.external_id").eq(field("a.customer_external_id")))
                .where(field("c.uuid").eq(uuid))
                .fetch(customerAddressRecordMapper("c", "a"));

        if (customerPerAddress.isEmpty()) {
            return Optional.empty();
        }

        final List<Address> addresses = customerPerAddress
                .stream()
                .flatMap(it -> it.getAddresses().stream())
                .map(it -> new Address(
                        it.getExternalId(),
                        it.getUuid(),
                        it.getCustomerExternalId(),
                        it.getName(),
                        it.getLine1(),
                        it.getLine2(),
                        it.getCity(),
                        it.getCountry(),
                        it.getLatitude(),
                        it.getLongitude(),
                        it.getCreatedDate(),
                        it.getCreatedBy(),
                        it.getLastModifiedDate(),
                        it.getLastModifiedBy(),
                        it.getVersion()
                )).collect(Collectors.toList());

        final Customer customer = customerPerAddress.get(0);
        return Optional.of(new Customer(
                customer.getExternalId(),
                customer.getUuid(),
                customer.getFirstName(),
                customer.getLastName(),
                addresses,
                customer.getCreatedDate(),
                customer.getCreatedBy(),
                customer.getLastModifiedDate(),
                customer.getLastModifiedBy(),
                customer.getVersion()
        ));
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
                // TODO: Avoid using asterisk
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
                record.get("created_date", new OffsetDateTimeConverter()),
                record.get("created_by", String.class),
                record.get("last_modified_date", new OffsetDateTimeConverter()),
                record.get("last_modified_by", String.class),
                record.get("version", Long.class)
        );
    }

    private RecordMapper<Record, Customer> customerAddressRecordMapper(String customerAlias, String addressAlias) {
        final Function<String, String> c = (fieldName) -> customerAlias.concat(".").concat(fieldName);
        Function<String, String> a = (fieldName) -> addressAlias.concat(".").concat(fieldName);

        return record -> new Customer(
                record.get(c.apply("external_id"), Long.class),
                record.get(c.apply("uuid"), String.class),
                record.get(c.apply("first_name"), String.class),
                record.get(c.apply("last_name"), String.class),
                Collections.singletonList(new Address(
                        record.get(a.apply("external_id"), Long.class),
                        record.get(a.apply("uuid"), String.class),
                        record.get(a.apply("customer_external_id"), Long.class),
                        record.get(a.apply("name"), String.class),
                        record.get(a.apply("line_1"), String.class),
                        record.get(a.apply("line_2"), String.class),
                        record.get(a.apply("city"), String.class),
                        new CountryCode(record.get(a.apply("country"), String.class)),
                        record.get(a.apply("latitude"), BigDecimal.class),
                        record.get(a.apply("longitude"), BigDecimal.class),
                        record.get(a.apply("created_date"), new OffsetDateTimeConverter()),
                        record.get(a.apply("created_by"), String.class),
                        record.get(a.apply("last_modified_date"), new OffsetDateTimeConverter()),
                        record.get(a.apply("created_by"), String.class),
                        record.get(a.apply("version"), Long.class)
                )),
                record.get(c.apply("created_date"), new OffsetDateTimeConverter()),
                record.get(c.apply("created_by"), String.class),
                record.get(c.apply("last_modified_date"), new OffsetDateTimeConverter()),
                record.get(c.apply("last_modified_by"), String.class),
                record.get(c.apply("version"), Long.class)
        );
    }
}
