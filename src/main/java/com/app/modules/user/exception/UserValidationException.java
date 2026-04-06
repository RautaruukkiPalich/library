package com.app.modules.user.exception;

import com.app.core.exception.ValidationException;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class UserValidationException extends ValidationException {

    public UserValidationException(@NonNull String field, @NonNull String message) {
        super("validation error", field, message);
    }

    public UserValidationException(@NonNull Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }
}
