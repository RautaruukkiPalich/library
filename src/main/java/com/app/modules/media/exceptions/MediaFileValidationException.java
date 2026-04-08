package com.app.modules.media.exceptions;

import com.app.core.exception.ValidationException;
import lombok.NonNull;

import java.util.Map;

public class MediaFileValidationException extends ValidationException {

    public MediaFileValidationException(@NonNull String field, @NonNull String message) {
        super("validation error", field, message);
    }

    public MediaFileValidationException(@NonNull Map<String, String> errorsMap) {
        super("validation error", errorsMap);
    }

}
