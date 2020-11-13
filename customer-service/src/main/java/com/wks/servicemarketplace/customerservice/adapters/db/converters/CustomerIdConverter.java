package com.wks.servicemarketplace.customerservice.adapters.db.converters;

import com.wks.servicemarketplace.customerservice.core.usecase.customer.CustomerId;
import org.jooq.Converter;

public class CustomerIdConverter implements Converter<Long, CustomerId> {

    @Override
    public CustomerId from(Long id) {
        return id == null ? null : CustomerId.of(id);
    }

    @Override
    public Long to(CustomerId id) {
        return id == null ? null : id.getId();
    }

    @Override
    public Class<Long> fromType() {
        return Long.class;
    }

    @Override
    public Class<CustomerId> toType() {
        return CustomerId.class;
    }
}
