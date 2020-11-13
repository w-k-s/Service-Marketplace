package com.wks.servicemarketplace.customerservice.core.usecase.customer;


import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.customerservice.core.usecase.Id;

public class CustomerId extends Id<Long> {

    private CustomerId(Long id) {
        super(id);
    }

    public static CustomerId of(Long id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(id >= 0);
        return new CustomerId(id);
    }
}
