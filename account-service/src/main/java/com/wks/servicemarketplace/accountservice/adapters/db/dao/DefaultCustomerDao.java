package com.wks.servicemarketplace.accountservice.adapters.db.dao;

import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.models.Address;
import com.wks.servicemarketplace.accountservice.core.models.Customer;
import org.jooq.Field;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

import static com.wks.servicemarketplace.accountservice.adapters.db.converters.JOOQConverters.zonedDateTime;
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
                field("created_date", zonedDateTime()),
                field("created_by")
        ).values(
                customer.getExternalId(),
                customer.getUuid(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getCreatedDate(),
                customer.getCreatedBy()
        ).execute();
    }

    @Override
    public Long newAddressExternalId(Connection connection) {
        return create(connection).nextval(sequence("address_external_id")).longValue();
    }

    @Override
    public void saveAddress(Connection connection, Address address) {
        final Field location = field(String.format("'SRID=4326;POINT(%s %s)'",
                address.getLongitude().toString(),
                address.getLatitude().toString()
        ));

        create(connection).insertInto(
                table("addresses"),
                field("external_id"),
                field("customer_external_id"),
                field("name"),
                field("line_1"),
                field("line_2"),
                field("city"),
                field("country_code"),
                field("location"),
                field("created_date", zonedDateTime()),
                field("created_by")
        ).values(
                address.getExternalId(),
                address.getCustomerExternalId(),
                address.getName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getCountry().getCountryCode(),
                location,
                address.getCreatedDate(),
                address.getCreatedBy()
        ).execute();

    }
}
