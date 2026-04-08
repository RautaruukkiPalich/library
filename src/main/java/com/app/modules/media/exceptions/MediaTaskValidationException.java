package com.app.modules.media.exceptions;

import com.app.core.exception.ValidationException;
import lombok.NonNull;

import java.util.Map;

public class MediaTaskValidationException extends ValidationException {

    public MediaTaskValidationException(@NonNull String field, @NonNull String message) {
        super("validation error", field, message);
    }

    public MediaTaskValidationException(@NonNull Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }

}
