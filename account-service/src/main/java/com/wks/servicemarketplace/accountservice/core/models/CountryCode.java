package com.wks.servicemarketplace.accountservice.core.models;

import com.wks.servicemarketplace.accountservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Optional;

@Value
public class CountryCode {

    @NonNull
    private final String countryCode;

    public CountryCode(String countryCode) throws UseCaseException {
        this.countryCode = Optional.ofNullable(com.neovisionaries.i18n.CountryCode.getByAlpha2Code(countryCode))
                .map(com.neovisionaries.i18n.CountryCode::name)
                .orElseThrow(() -> new UseCaseException(
                        ErrorType.INVALID_COUNTRY,
                        Collections.singletonMap("countryCode", countryCode)));
    }
}
