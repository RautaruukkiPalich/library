package com.app.modules.media.exceptions;

import com.app.core.exception.NotFoundException;
import lombok.NonNull;

import java.util.UUID;

public class MediaTaskNotFound extends NotFoundException {
    public MediaTaskNotFound(String message) {
        super(message);
    }

    public MediaTaskNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaTaskNotFound(@NonNull UUID uuid){
        super("task not found with uuid: %s".formatted(uuid));
    }
}
