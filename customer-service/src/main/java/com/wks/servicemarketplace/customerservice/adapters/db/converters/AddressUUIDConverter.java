package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import com.wks.servicemarketplace.customerservice.api.AddressUUID;
import org.jooq.Converter;

public class AddressUUIDConverter implements Converter<String, AddressUUID> {

    @Override
    public AddressUUID from(String id) {
        return id == null ? null : AddressUUID.of(id);
    }

    @Override
    public String to(AddressUUID id) {
        return id == null ? null : id.getId().toString();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<AddressUUID> toType() {
        return AddressUUID.class;
    }
}
