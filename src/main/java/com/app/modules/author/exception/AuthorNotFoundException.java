package com.app.modules.author.exception;

import com.app.core.exception.NotFoundException;
import lombok.Getter;

@Getter
public class AuthorNotFoundException extends NotFoundException {
    private final Long id;

    public AuthorNotFoundException(Long id) {
        super("author not found with id: " + id);
        this.id = id;
    }

}
