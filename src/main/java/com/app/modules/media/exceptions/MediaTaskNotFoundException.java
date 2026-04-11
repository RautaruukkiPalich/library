package com.app.modules.media.exceptions;

import com.app.core.exception.NotFoundException;
import lombok.NonNull;

import java.util.UUID;

public class MediaTaskNotFoundException extends NotFoundException {
    public MediaTaskNotFoundException(String message) {
        super(message);
    }

    public MediaTaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static MediaTaskNotFoundException uuid(@NonNull UUID uuid) {
        return new MediaTaskNotFoundException("task with uuid %s not found".formatted(uuid));
    }
}
