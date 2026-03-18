package com.app.exception.notfound;

public class UserNotFoundException extends NotFoundException{
    public UserNotFoundException(String field, String value) {
        super(String.format("user not found with %s: %s", field, value));
    }

    public UserNotFoundException(Long id) {
        super("user not found with id: " + id);
    }
}
