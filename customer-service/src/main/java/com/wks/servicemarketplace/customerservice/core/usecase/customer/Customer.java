package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
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
    @Size(min = 1, max = 5)
    private List<Address> addresses;

    private OffsetDateTime createdDate;

    @NonNull
    @NotBlank
    private String createdBy;

    private OffsetDateTime lastModifiedDate;
    private String lastModifiedBy;
    private long version;

    public Customer(Long externalId,
                    String uuid,
                    String firstName,
                    String lastName,
                    List<Address> addresses,
                    OffsetDateTime createdDate,
                    String createdBy,
                    OffsetDateTime lastModifiedDate,
                    String lastModifiedBy,
                    long version) {
        this.id = 0L;
        this.externalId = externalId;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = addresses;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

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
                null,
                createdBy,
                null,
                null,
                0
        );

        return ResultWithEvents.of(ModelValidator.validate(customer), Collections.singletonList(CustomerCreatedEvent.of(customer)));
    }

    public ResultWithEvents<Customer, AddressAddedEvent> addAddress(Address address, final String modifiedBy) {
        final List<Address> addresses = new ArrayList<>(this.addresses);
        addresses.add(address);

        final Customer customer = new Customer(
                this.id,
                this.externalId,
                this.uuid,
                this.firstName,
                this.lastName,
                Collections.unmodifiableList(addresses),
                this.createdDate,
                this.createdBy,
                OffsetDateTime.now(ZoneId.of("UTC")),
                modifiedBy,
                0
        );

        return ResultWithEvents.of(ModelValidator.validate(customer), Collections.singletonList(AddressAddedEvent.of(
                address
        )));
    }
}
