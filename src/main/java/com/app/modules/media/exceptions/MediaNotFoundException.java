package com.app.modules.media.exceptions;

import java.util.UUID;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(String message) {
        super(message);
    }

    public MediaNotFoundException(UUID uuid) {
        super("media with uuid %s not found".formatted(uuid));
    }
}
