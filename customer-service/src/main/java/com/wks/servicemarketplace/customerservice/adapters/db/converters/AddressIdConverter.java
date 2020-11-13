package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressId;
import org.jooq.Converter;

public class AddressIdConverter implements Converter<Long, AddressId> {

    @Override
    public AddressId from(Long id) {
        return id == null ? null : AddressId.of(id);
    }

    @Override
    public Long to(AddressId id) {
        return id == null ? null : id.getId();
    }

    @Override
    public Class<Long> fromType() {
        return Long.class;
    }

    @Override
    public Class<AddressId> toType() {
        return AddressId.class;
    }
}
