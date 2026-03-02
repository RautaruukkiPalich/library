package com.app.exception.validation;

import java.util.Map;

public class BookValidationException extends ValidationException {

    public BookValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public BookValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}