package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.customerservice.core.usecase.ResultWithEvents;
import com.wks.servicemarketplace.customerservice.core.usecase.address.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressAddedEvent;
import com.wks.servicemarketplace.customerservice.core.usecase.address.AddressId;
import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Value
public class Customer {

    private Long id;

    @NonNull
    private CustomerId externalId;

    @NonNull
    @NotBlank
    private CustomerUUID uuid;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NonNull
    private Collection<AddressId> addresses;

    private OffsetDateTime createdDate;

    @NonNull
    @NotBlank
    private String createdBy;

    private OffsetDateTime lastModifiedDate;
    private String lastModifiedBy;
    private long version;

    private Customer(Long id,
                     CustomerId externalId,
                     CustomerUUID uuid,
                     String firstName,
                     String lastName,
                     Collection<AddressId> addresses,
                     OffsetDateTime createdDate,
                     String createdBy,
                     OffsetDateTime lastModifiedDate,
                     String lastModifiedBy,
                     long version) {
        this.id = id;
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

    public static ResultWithEvents<Customer, CustomerCreatedEvent> create(final CustomerId externalId,
                                                                          final String firstName,
                                                                          final String lastName,
                                                                          final String createdBy) {
        final Customer customer = new Customer(
                0L,
                externalId,
                CustomerUUID.random(),
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private CustomerId externalId;
        private CustomerUUID uuid;
        private String firstName;
        private String lastName;
        private Collection<AddressId> addresses;
        private OffsetDateTime createdDate;
        private String createdBy;
        private OffsetDateTime lastModifiedDate;
        private String lastModifiedBy;
        private long version;

        private Builder() {
        }

        public Long getId() {
            return id;
        }

        public CustomerId getExternalId() {
            return externalId;
        }

        public CustomerUUID getUuid() {
            return uuid;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public Collection<AddressId> getAddresses() {
            return addresses;
        }

        public OffsetDateTime getCreatedDate() {
            return createdDate;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public OffsetDateTime getLastModifiedDate() {
            return lastModifiedDate;
        }

        public String getLastModifiedBy() {
            return lastModifiedBy;
        }

        public long getVersion() {
            return version;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder externalId(CustomerId externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder uuid(CustomerUUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder addresses(Collection<AddressId> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder createdDate(OffsetDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder lastModifiedDate(OffsetDateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public Builder lastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Customer build() {
            return new Customer(id, externalId, uuid, firstName, lastName, addresses, createdDate, createdBy, lastModifiedDate, lastModifiedBy, version);
        }
    }
}
