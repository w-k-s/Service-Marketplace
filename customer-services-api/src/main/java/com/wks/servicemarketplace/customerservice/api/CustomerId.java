package com.wks.servicemarketplace.customerservice.api;


import com.google.common.base.Preconditions;

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
