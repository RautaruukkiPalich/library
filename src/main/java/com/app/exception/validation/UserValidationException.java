package com.app.exception.validation;

import java.util.Map;

public class UserValidationException extends ValidationException {

    public UserValidationException(String field, String message) {
        super("validation error", field, message);
    }

    public UserValidationException(Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}
