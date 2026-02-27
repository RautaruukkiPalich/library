package com.app.exception;

public class AuthorNotFoundException extends NotFoundException{
    private final Long id;

    public AuthorNotFoundException(Long id) {
        super("author not found with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
