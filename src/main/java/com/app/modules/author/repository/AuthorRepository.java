package com.app.modules.author.repository;

import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.exception.AuthorNotFoundException;
import com.app.modules.author.model.Author;

import java.util.List;

public interface AuthorRepository {
    List<Author> getAll();

    List<Author> getAll(AuthorFilter filter);

    Author getByID(Long id) throws AuthorNotFoundException;

    Author save(Author author);

    void delete(Author author);
}
