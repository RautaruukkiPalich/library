package com.app.modules.auth.exception;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class RefreshTokenValidationException extends ValidationException {
    public RefreshTokenValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public RefreshTokenValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}
