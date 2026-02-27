package com.app.exception;

public class GenreNotFoundException extends NotFoundException {
    private final Long id;

    public GenreNotFoundException(Long id) {
        super("genre not found with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
