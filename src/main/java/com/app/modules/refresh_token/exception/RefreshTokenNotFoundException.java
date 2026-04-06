package com.app.modules.refresh_token.exception;

import com.app.core.exception.NotFoundException;

public class RefreshTokenNotFoundException extends NotFoundException {
    public RefreshTokenNotFoundException(String message) {
        super("refresh token not found: " + message);
    }

    public RefreshTokenNotFoundException() {
        super("refresh token not found");
    }
}
