package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.customerservice.core.exceptions.CountryCodeNotFoundException;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class CountryCode {

    @NonNull
    private final String countryCode;

    public CountryCode(String countryCode) throws CountryCodeNotFoundException {
        this.countryCode = Optional.ofNullable(com.neovisionaries.i18n.CountryCode.getByAlpha2Code(countryCode))
                .map(com.neovisionaries.i18n.CountryCode::name)
                .orElseThrow(() -> new CountryCodeNotFoundException(countryCode, "ISO 3166-1 alpha-2"));
    }
}
