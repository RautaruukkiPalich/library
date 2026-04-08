package com.app.modules.media.exceptions;

import com.app.core.exception.NotFoundException;

import java.util.UUID;

public class MediaFileNotFoundException extends NotFoundException {
    public MediaFileNotFoundException(String message) {
        super(message);
    }

    public MediaFileNotFoundException(UUID uuid) {
        super("media file with uuid %s not found".formatted(uuid));
    }
}
