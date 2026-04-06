package com.app.modules.user.exception;

import com.app.core.exception.NotFoundException;
import org.jspecify.annotations.NonNull;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String field, String value) {
        super(String.format("user not found with %s: %s", field, value));
    }

    public UserNotFoundException(@NonNull Long id) {
        super("user not found with id: " + id);
    }
}
