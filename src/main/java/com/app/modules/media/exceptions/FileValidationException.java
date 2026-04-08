package com.app.modules.media.exceptions;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class FileValidationException extends ValidationException {

    public FileValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public FileValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}

