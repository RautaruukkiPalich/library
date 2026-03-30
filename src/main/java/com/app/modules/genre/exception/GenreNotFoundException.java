package com.app.modules.genre.exception;

import com.app.core.exception.NotFoundException;
import lombok.Getter;

@Getter
public class GenreNotFoundException extends NotFoundException {
    private final Long id;

    public GenreNotFoundException(Long id) {
        super("genre not found with id: " + id);
        this.id = id;
    }

}
