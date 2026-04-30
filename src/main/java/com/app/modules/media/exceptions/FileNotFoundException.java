package com.app.modules.media.exceptions;

import com.app.core.exception.NotFoundException;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(String message) {
        super(message);
    }

    public static FileNotFoundException path(String path) {
        return new FileNotFoundException("file not found by path %s".formatted(path));
    }
}
