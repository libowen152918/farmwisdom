package com.farmwisdom.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String message;
    private final Integer code;
    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
        this.errorCode = "INTERNAL_ERROR";
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
        this.errorCode = "INTERNAL_ERROR";
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.message = message;
        this.code = 500;
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
        this.errorCode = errorCode;
    }
} 