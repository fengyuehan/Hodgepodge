package com.example.httplibrary.convert;

public class ApiException extends RuntimeException {

    private String errorCode;

    public ApiException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
