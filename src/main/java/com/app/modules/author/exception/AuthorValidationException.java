package com.app.modules.author.exception;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class AuthorValidationException extends ValidationException {

    public AuthorValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public AuthorValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}