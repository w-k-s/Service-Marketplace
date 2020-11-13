package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.customerservice.core.usecase.Id;
import jdk.internal.jline.internal.Preconditions;

import java.util.UUID;

public final class CustomerUUID extends Id<UUID> {

    private CustomerUUID(UUID uuid) {
        super(uuid);
    }

    public static CustomerUUID of(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return new CustomerUUID(uuid);
    }

    public static CustomerUUID of(String uuid) {
        Preconditions.checkNotNull(uuid);
        return new CustomerUUID(UUID.fromString(uuid));
    }

    public static CustomerUUID random() {
        return new CustomerUUID(UUID.randomUUID());
    }
}
