package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.customerservice.core.usecase.Id;
import jdk.internal.jline.internal.Preconditions;

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
