package com.wks.servicemarketplace.customerservice.core.exceptions;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Map;

public class CoreRuntimeException extends RuntimeException {

    private final ErrorType errorType;
    private final String description;
    private final Map<String, String> userInfo;

    public CoreRuntimeException(ErrorType errorType, String description) {
        this(errorType, description, Collections.emptyMap(), null);
    }

    public CoreRuntimeException(ErrorType errorType, Map<String, String> userInfo) {
        this(errorType, null, userInfo, null);
    }

    public CoreRuntimeException(ErrorType errorType, Throwable cause) {
        this(errorType, cause.getMessage(), Collections.emptyMap(), cause);
    }

    public CoreRuntimeException(ErrorType errorType, String description, Map<String, String> userInfo, Throwable cause) {
        super(description, cause);
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
