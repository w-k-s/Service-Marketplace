package com.wks.servicemarketplace.customerservice.core.exceptions;

import com.google.common.collect.ImmutableMap;

public class CountryCodeNotFoundException extends CoreException {

    private final String countryCode;

    public CountryCodeNotFoundException(String countryCode, String isoStandard) {
        super(
                ErrorType.INVALID_COUNTRY,
                String.format("'%s' is not a known '%s' country code", countryCode, isoStandard),
                ImmutableMap.of("countryCode", countryCode),
                null
        );
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
