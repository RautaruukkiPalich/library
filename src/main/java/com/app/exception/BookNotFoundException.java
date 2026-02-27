package com.app.exception;

public class BookNotFoundException extends NotFoundException{
    private final Long id;

    public BookNotFoundException(Long id) {
        super("book not found with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}


