package com.app.modules.media.exceptions;

import java.util.Objects;

public class FileUploadException extends RuntimeException{
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable e) {
        super(message, e);
    }
}
