package com.wks.servicemarketplace.accountservice.core.utils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    private final Map<String, String> fields;

    public ValidationException(Map<String, String> fields) {
        super(buildErrorMessage(fields));
        this.fields = Collections.unmodifiableMap(fields);
    }

    private static String buildErrorMessage(final Map<String, String> fields) {
        return fields.entrySet()
                .stream()
                .map(entry -> String.format("%s: %S", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","));
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
