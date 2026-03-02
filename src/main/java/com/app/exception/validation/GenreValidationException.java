package com.app.exception.validation;

import java.util.Map;

public class GenreValidationException extends ValidationException {

    public GenreValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public GenreValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}