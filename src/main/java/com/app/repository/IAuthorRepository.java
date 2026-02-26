package com.app.repository;

import java.util.List;

import com.app.exception.AuthorNotFoundException;
import com.app.filter.AuthorFilter;
import com.app.model.Author;

public interface IAuthorRepository {
    List<Author> getAll();
    List<Author> getAll(AuthorFilter filter);
    Author getByID(Long id) throws AuthorNotFoundException;
    Author save(Author author);
    void deleteByID(Long id);
}
