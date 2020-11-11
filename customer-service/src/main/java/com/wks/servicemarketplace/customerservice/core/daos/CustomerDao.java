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
    void saveAddress(Connection connection, Address address);

    List<Address> findAddressesByCustomerUuid(Connection connection, String customerUuid) throws SQLException;
    Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, long customerId, long addressId) throws SQLException;
}
