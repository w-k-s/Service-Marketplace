package com.wks.servicemarketplace.accountservice.core.models;

import com.wks.servicemarketplace.accountservice.core.models.events.CustomerCreatedEvent;
import com.wks.servicemarketplace.accountservice.core.utils.ModelValidator;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {

    private Long id;

    @NonNull
    @PositiveOrZero
    private Long externalId;

    @NonNull
    @NotBlank
    private String uuid;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NonNull
    private List<Address> addresses;

    @NonNull
    @NotNull
    private ZonedDateTime createdDate;

    @NonNull
    @NotBlank
    private String createdBy;

    private ZonedDateTime lastModifiedDate;
    private String lastModifiedBy;
    private long version;

    public static ResultWithEvents<Customer, CustomerCreatedEvent> create(final Long externalId,
                                                                          final String firstName,
                                                                          final String lastName,
                                                                          final String createdBy) {
        final Customer customer = new Customer(
                0L,
                externalId,
                UUID.randomUUID().toString(),
                firstName,
                lastName,
                Collections.emptyList(),
                ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
                createdBy,
                null,
                null,
                0);
        ModelValidator.validate(customer);

        return ResultWithEvents.of(customer, Collections.singletonList(CustomerCreatedEvent.of(customer)));
    }
}
