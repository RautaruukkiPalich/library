package com.app.modules.media.exceptions;

import java.util.UUID;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(String message) {
        super(message);
    }

    static public MediaNotFoundException uuid(UUID uuid) {
        return new MediaNotFoundException("media with uuid %s not found".formatted(uuid));
    }
}
