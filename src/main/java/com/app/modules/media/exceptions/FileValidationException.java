package com.app.modules.media.exceptions;

import com.app.core.exception.ValidationException;

import java.util.Map;

public class FileValidationException extends ValidationException {

    public FileValidationException(String field, String message) {
        super("file validation error", field, message);
    }

    public FileValidationException(Map<String, String> errorsMap) {
        super("file validation error", errorsMap);
    }
}

