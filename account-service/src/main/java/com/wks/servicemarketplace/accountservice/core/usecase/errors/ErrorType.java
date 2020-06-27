package com.wks.servicemarketplace.accountservice.core.usecase.errors;

import org.eclipse.jetty.http.HttpStatus;

public enum ErrorType {
    UNKNOWN(ErrorType.GENERAL_ERROR_UNKNOWN),
    VALIDATION(ErrorType.GENERAL_ERROR_VALIDATION),
    NOT_FOUND(ErrorType.GENERAL_ERROR_NOT_FOUND),
    INVALID_STATE(ErrorType.GENERAL_ERROR_INVALID_STATE),
    DATABASE(ErrorType.GENERAL_ERROR_DATABASE),
    CUSTOMER_NOT_CREATED(ErrorType.CUSTOMER_ERROR_NOT_CREATED),
    ADDRESS_NOT_CREATED(ErrorType.ADDRESS_ERROR_NOT_CREATED),
    INVALID_COUNTRY(ErrorType.ADDRESS_ERROR_INVALID_COUNTRY),
    ADDRESS_NOT_FOUND(ErrorType.ADDRESS_ERROR_NOT_FOUND),
    ADDRESS_OUTDATED(ErrorType.ADDRESS_ERROR_OUTDATED);

    private static final int SERVICE_CODE = 2_00_000;

    private static final int CATEGORY_CODE_GENERAL_ERROR = 0;
    private static final int GENERAL_ERROR_UNKNOWN = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 0;                   // 200000
    private static final int GENERAL_ERROR_VALIDATION = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 1;                // 200001
    private static final int GENERAL_ERROR_NOT_FOUND = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 10;                // 200010
    private static final int GENERAL_ERROR_INVALID_STATE = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 20;            // 200020
    private static final int GENERAL_ERROR_DATABASE = SERVICE_CODE + CATEGORY_CODE_GENERAL_ERROR + 30;                  // 200030

    private static final int CATEGORY_CODE_CUSTOMER_ERROR = 1_000;
    private static final int CUSTOMER_ERROR_NOT_CREATED = SERVICE_CODE + CATEGORY_CODE_CUSTOMER_ERROR + 1;               // 201001

    private static final int CATEGORY_CODE_ADDRESS_ERROR = 2_000;
    private static final int ADDRESS_ERROR_NOT_CREATED = SERVICE_CODE + CATEGORY_CODE_ADDRESS_ERROR + 1;                // 202001
    private static final int ADDRESS_ERROR_INVALID_COUNTRY = SERVICE_CODE + CATEGORY_CODE_ADDRESS_ERROR + 2;            // 202002
    private static final int ADDRESS_ERROR_NOT_FOUND = SERVICE_CODE + CATEGORY_CODE_ADDRESS_ERROR + 3;                  // 202003
    private static final int ADDRESS_ERROR_OUTDATED = SERVICE_CODE + CATEGORY_CODE_ADDRESS_ERROR + 4;                   // 202004

    public final int code;

    private ErrorType(int code) {
        this.code = code;
    }
    }
