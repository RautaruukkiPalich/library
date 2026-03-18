package com.app.modules.genre.exception;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class GenreValidationException extends ValidationException {

    public GenreValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public GenreValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}