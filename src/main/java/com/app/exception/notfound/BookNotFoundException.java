package com.app.exception.notfound;

import lombok.Getter;

@Getter
public class BookNotFoundException extends NotFoundException {
    private final Long id;

    public BookNotFoundException(Long id) {
        super("book not found with id: " + id);
        this.id = id;
    }

}


