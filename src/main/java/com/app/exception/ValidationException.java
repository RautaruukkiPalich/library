package com.app.exception;

import java.util.HashMap;

public abstract class ValidationException extends RuntimeException {
    private final HashMap<String, String> errorsMap;

    public ValidationException(String message, String field, String reason) {
        super(message);

        this.errorsMap = new HashMap<>();
        this.errorsMap.put(field, reason);
    }

    public ValidationException(String message, HashMap<String, String> errorsMap) {
        super(message);
        this.errorsMap = errorsMap;
    }

    public HashMap<String, String> getErrorsMap() {
        return errorsMap;
    }
}
