package com.hospivet.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> fieldErrors;

    public BusinessException(HttpStatus status, String message) {
        this(status, message, Map.of());
    }

    /** Permite identificar el campo y la regla incumplida (p. ej. R9.2.3). */
    public BusinessException(HttpStatus status, String message, Map<String, String> fieldErrors) {
        super(message);
        this.status = status;
        this.fieldErrors = fieldErrors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}

