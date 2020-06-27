package com.wks.servicemarketplace.accountservice.core.exceptions;

public class CountryCodeNotFoundException extends RuntimeException {

    private final String countryCode;

    public CountryCodeNotFoundException(String countryCode) {
        super(countryCode);
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
