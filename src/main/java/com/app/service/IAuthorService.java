package com.app.service;

import com.app.dto.AuthorDTO;
import com.app.filter.AuthorFilter;
import com.app.model.Author;

import java.util.List;

public interface IAuthorService {
    List<Author> getAll();
    List<Author> getAll(AuthorFilter filter);
    void add(AuthorDTO dto);
    Author getByID(Long id);
    void delete(Long id);
}
