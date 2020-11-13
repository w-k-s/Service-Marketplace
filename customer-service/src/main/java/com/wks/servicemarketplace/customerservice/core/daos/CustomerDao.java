package com.wks.servicemarketplace.customerservice.core.daos;

import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDao extends Dao{
    Long newCustomerExternalId(Connection connection);
    void saveCustomer(Connection connection, Customer customer) throws SQLException;

    Long newAddressExternalId(Connection connection);
    //TODO: Remove this. Should load aggregate root (i.e. Customer) and save through aggregate root.
    void saveAddress(Connection connection, Address address);

    Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, long customerId, long addressId) throws SQLException;

    Optional<Customer> findCustomerByUuid(Connection connection, String uuid);
}
