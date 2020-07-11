package com.wks.servicemarketplace.accountservice.core.exceptions;

public class CountryCodeNotFoundException extends RuntimeException {

    private final String countryCode;

    public CountryCodeNotFoundException(String countryCode, String isoStandard){
        super(String.format("'%s' is not a known '%s' country code", countryCode, isoStandard));
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
