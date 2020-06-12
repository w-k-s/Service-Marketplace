package com.wks.servicemarketplace.accountservice.adapters.web.errors;

import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final int code;
    @NonNull
    private final String name;
    private final String description;
    private final Map<String, String> userInfo;

    public ErrorResponse(ErrorType errorType, String description, Map<String, String> userInfo) {
        this.code = errorType.code;
        this.name = errorType.name();
        this.description = description;
        this.userInfo = userInfo == null ? Collections.emptyMap() : Collections.unmodifiableMap(userInfo);
    }
}
