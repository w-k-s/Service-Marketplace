package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.wks.servicemarketplace.customerservice.core.utils.ModelValidator;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Name {
    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    public final String firstName;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 50)
    public final String lastName;

    private Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Name of(String firstName, String lastName) {
        return ModelValidator.validate(new Name(firstName, lastName), Name.class);
    }

    @Override
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}
