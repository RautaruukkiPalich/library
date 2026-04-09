package com.app.modules.media.exceptions;

import com.app.core.exception.NotFoundException;
import com.app.modules.media.enums.MediaSize;

import java.util.UUID;

public class MediaFileNotFoundException extends NotFoundException {
    public MediaFileNotFoundException(String message) {
        super(message);
    }

    public MediaFileNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public static MediaFileNotFoundException uuid(UUID uuid) {
        return new MediaFileNotFoundException("media file with uuid %s not found".formatted(uuid));
    }

    public static MediaFileNotFoundException size(MediaSize size) {
        return new MediaFileNotFoundException("media file with size %s not found".formatted(size));
    }
}
