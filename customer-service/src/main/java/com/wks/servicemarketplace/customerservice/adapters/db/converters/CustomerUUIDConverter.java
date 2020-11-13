package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressUUID;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerUUID;
import org.jooq.Converter;

public class CustomerUUIDConverter implements Converter<String, CustomerUUID> {

    @Override
    public CustomerUUID from(String id) {
        return id == null ? null : CustomerUUID.of(id);
    }

    @Override
    public String to(CustomerUUID id) {
        return id == null ? null : id.getId().toString();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<CustomerUUID> toType() {
        return CustomerUUID.class;
    }
}
