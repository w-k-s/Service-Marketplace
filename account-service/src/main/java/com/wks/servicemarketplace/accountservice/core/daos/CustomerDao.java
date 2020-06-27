package com.wks.servicemarketplace.accountservice.core.daos;

import com.wks.servicemarketplace.accountservice.core.models.Address;
import com.wks.servicemarketplace.accountservice.core.models.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface CustomerDao extends Dao{
    Long newCustomerExternalId(Connection connection);
    void saveCustomer(Connection connection, Customer customer) throws SQLException;

    Long newAddressExternalId(Connection connection);
    void saveAddress(Connection connection, Address address);

    Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, long customerId, long addressId) throws SQLException;
}
