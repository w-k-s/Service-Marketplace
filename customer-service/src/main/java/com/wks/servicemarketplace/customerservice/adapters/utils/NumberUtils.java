package com.wks.servicemarketplace.customerservice.adapters.utils;

import java.util.Optional;

public class NumberUtils {
    public static Optional<Long> parseLong(String number) {
        try {
            return Optional.of(Long.parseLong(number));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
