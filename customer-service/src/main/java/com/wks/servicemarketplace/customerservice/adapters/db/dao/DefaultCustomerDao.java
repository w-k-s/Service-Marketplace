package com.wks.servicemarketplace.customerservice.adapters.db.dao;

import com.wks.servicemarketplace.common.AddressId;
import com.wks.servicemarketplace.common.CountryCode;
import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.customerservice.adapters.db.converters.*;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

public class DefaultCustomerDao extends BaseDAO implements CustomerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerDao.class);

    @Inject
    public DefaultCustomerDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CustomerId newCustomerExternalId(Connection connection) {
        return CustomerId.of(create(connection)
                .nextval(sequence(DSL.name("customer_external_id"), Long.class)));
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
                customer.getExternalId().getValue(),
                customer.getUuid().toString(),
                customer.getName().getFirstName(),
                customer.getName().getLastName(),
                customer.getCreatedBy()
        ).execute();
    }

    @Override
    public Optional<Customer> findCustomerByUuid(Connection connection, CustomerUUID uuid) {

        return Optional.ofNullable(
                create(connection)
                        .select(
                                field("c.external_id"),
                                field("c.uuid"),
                                field("c.first_name"),
                                field("c.last_name"),
                                field("c.created_date"),
                                field("c.created_by"),
                                field("c.last_modified_date"),
                                field("c.last_modified_by"),
                                field("c.version")
                        )
                        .from(table("customers").as("c"))
                        .where(field("c.uuid").eq(uuid.toString()))
                        .fetchOne(customerRecordMapper())
        ).map(it -> {
            it.addresses(
                    Optional.of(create(connection)
                            .select(
                                    field("a.external_id")
                            ).from(table("addresses").as("a"))
                            .where(field("a.customer_external_id").eq(it.getExternalId()))
                            .fetchInto(Long.class)
                    ).orElseGet(Collections::emptyList)
                            .stream()
                            .map(AddressId::of)
                            .collect(Collectors.toList())
            );
            return it;
        }).map(Customer.Builder::build);
    }

    @Override
    public AddressId newAddressExternalId(Connection connection) {
        return AddressId.of(create(connection)
                .nextval(sequence("address_external_id"))
                .longValue());
    }

    @Override
    public List<Address> findAddressesByCustomerUUID(Connection connection, CustomerUUID customerUUID) {
        return create(connection)
                .select(
                        field("a.external_id"),
                        field("a.uuid"),
                        field("a.customer_external_id"),
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
                .from(table("addresses").as("a"))
                .leftJoin(table("customers").as("c"))
                .on(field("a.customer_external_id").eq(field("c.external_id")))
                .where(field("c.uuid").eq(customerUUID.toString()))
                .fetch(addressRecordMapper());
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
                address.getCountry().toString(),
                address.getLatitude(),
                address.getLongitude(),
                address.getCreatedBy()
        ).execute();
    }

    @Override
    public Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, CustomerId customerId, AddressId addressId) throws SQLException {
        final Address address = create(connection)
                .select(
                        field("a.external_id"),
                        field("a.uuid"),
                        field("a.customer_external_id"),
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
                .from(table("addresses").as("a"))
                .where(field("a.external_id").eq(addressId))
                .and(field("a.customer_external_id").eq(customerId))
                .limit(1)
                .fetchOne(addressRecordMapper());
        return Optional.ofNullable(address);
    }

    private RecordMapper<Record, Address> addressRecordMapper() {
        return record -> new Address(
                record.get("a.external_id", new AddressIdConverter()),
                record.get("a.uuid", new AddressUUIDConverter()),
                record.get("a.customer_external_id", new CustomerIdConverter()),
                record.get("a.name", String.class),
                record.get("a.line_1", String.class),
                record.get("a.line_2", String.class),
                record.get("a.city", String.class),
                CountryCode.of(record.get("a.country_code", String.class)),
                record.get("a.latitude", BigDecimal.class),
                record.get("a.longitude", BigDecimal.class),
                record.get("a.created_date", new OffsetDateTimeConverter()),
                record.get("a.created_by", String.class),
                record.get("a.last_modified_date", new OffsetDateTimeConverter()),
                record.get("a.last_modified_by", String.class),
                record.get("a.version", Long.class)
        );
    }

    private RecordMapper<Record, Customer.Builder> customerRecordMapper() {
        return record -> Customer.builder()
                .id(record.get("c.id", Long.class))
                .externalId(record.get("c.external_id", new CustomerIdConverter()))
                .uuid(record.get("c.uuid", new CustomerUUIDConverter()))
                .firstName(record.get("c.first_name", String.class))
                .lastName(record.get("c.last_name", String.class))
                .createdDate(record.get("c.created_date", new OffsetDateTimeConverter()))
                .createdBy(record.get("c.created_by", String.class))
                .lastModifiedDate(record.get("c.last_modified_date", new OffsetDateTimeConverter()))
                .lastModifiedBy(record.get("c.last_modified_by", String.class))
                .version(record.get("c.version", Long.class));
    }
}
