package com.wks.servicemarketplace.customerservice.core.daos;

import com.wks.servicemarketplace.common.AddressId;
import com.wks.servicemarketplace.common.CustomerId;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDao extends Dao {

    CustomerId newCustomerExternalId(Connection connection);

    void saveCustomer(Connection connection, Customer customer) throws SQLException;

    AddressId newAddressExternalId(Connection connection);

    void saveAddress(Connection connection, Address address);

    Optional<Address> findAddressByAddressIdAndCustomerId(Connection connection, CustomerId customerId, AddressId addressId) throws SQLException;

    List<Address> findAddressesByCustomerUUID(Connection connection, CustomerUUID customerUUID);

    Optional<Customer> findCustomerByUuid(Connection connection, CustomerUUID uuid);
}
