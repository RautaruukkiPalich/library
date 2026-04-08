package com.app.modules.media.exceptions;

import com.app.core.exception.ForbiddenException;

public class MediaForbiddenException extends ForbiddenException {
    public MediaForbiddenException(String message) {
        super(message);
    }

    public MediaForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException("Insufficient permissions");
    }
}
