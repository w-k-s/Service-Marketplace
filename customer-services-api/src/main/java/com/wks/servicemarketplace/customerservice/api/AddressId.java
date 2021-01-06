package com.wks.servicemarketplace.customerservice.api;


import com.google.common.base.Preconditions;

public final class AddressId extends Id<Long> {

    private AddressId(long id) {
        super(id);
    }

    public static AddressId of(Long id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(id >= 0);
        return new AddressId(id);
    }
}
