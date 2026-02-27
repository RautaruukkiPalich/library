package com.app.exception;

import java.util.HashMap;

public class BookValidationException extends ValidationException {

    public BookValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public BookValidationException(HashMap<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}