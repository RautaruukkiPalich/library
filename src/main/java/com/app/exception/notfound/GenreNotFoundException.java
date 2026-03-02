package com.app.exception.notfound;

import lombok.Getter;

@Getter
public class GenreNotFoundException extends NotFoundException {
    private final Long id;

    public GenreNotFoundException(Long id) {
        super("genre not found with id: " + id);
        this.id = id;
    }

}
