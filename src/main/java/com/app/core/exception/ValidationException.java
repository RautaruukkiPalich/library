package com.app.core.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ValidationException extends RuntimeException {
    private final Map<String, String> errorsMap;

    public ValidationException(String message, String field, String reason) {
        super(message);

        this.errorsMap = new HashMap<>();
        this.errorsMap.put(field, reason);
    }

    public ValidationException(String message, Map<String, String> errorsMap) {
        super(message);
        this.errorsMap = errorsMap;
    }

}
