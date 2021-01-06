package com.wks.servicemarketplace.customerservice.api;

import com.google.common.base.Preconditions;

import java.util.UUID;

public final class AddressUUID extends Id<UUID> {

    private AddressUUID(UUID uuid) {
        super(uuid);
    }

    public static AddressUUID of(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return new AddressUUID(uuid);
    }

    public static AddressUUID of(String uuid) {
        Preconditions.checkNotNull(uuid);
        return new AddressUUID(UUID.fromString(uuid));
    }

    public static AddressUUID random() {
        return new AddressUUID(UUID.randomUUID());
    }
}
