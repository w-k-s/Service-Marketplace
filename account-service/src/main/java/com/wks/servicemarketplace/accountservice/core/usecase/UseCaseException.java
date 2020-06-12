package com.wks.servicemarketplace.accountservice.core.usecase;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.accountservice.core.usecase.errors.ErrorType;

import java.util.Collections;
import java.util.Map;

public class UseCaseException extends Exception {

    private final ErrorType errorType;
    private final String description;
    private final Map<String, String> userInfo;

    public UseCaseException(ErrorType errorType, String description) {
        this(errorType, description, Collections.emptyMap(), null);
    }

    public UseCaseException(ErrorType errorType, Map<String, String> userInfo) {
        this(errorType, null, userInfo, null);
    }

    public UseCaseException(ErrorType errorType, Throwable cause) {
        this(errorType, cause.getMessage(), Collections.emptyMap(), cause);
    }

    public UseCaseException(ErrorType errorType, String description, Map<String, String> userInfo, Throwable cause) {
        super(String.format("%s: %s", errorType, description), cause);
        Preconditions.checkNotNull(errorType);

        this.errorType = errorType;
        this.description = description;
        this.userInfo = userInfo == null ? Collections.emptyMap() : Collections.unmodifiableMap(userInfo);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Map<String, String> getUserInfo() {
        return userInfo;
    }

    public String getDescription() {
        return description;
    }
}
