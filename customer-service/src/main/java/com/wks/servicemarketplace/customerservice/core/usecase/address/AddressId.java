package com.wks.servicemarketplace.customerservice.core.usecase.address;


import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.customerservice.core.usecase.Id;

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
