package com.app.modules.book.exception;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class BookValidationException extends ValidationException {

    public BookValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public BookValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}