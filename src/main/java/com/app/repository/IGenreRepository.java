package com.app.repository;

import java.util.List;

import com.app.exception.GenreNotFoundException;
import com.app.filter.GenreFilter;
import com.app.model.Genre;

public interface IGenreRepository {
    List<Genre> getAll();
    List<Genre> getAll(GenreFilter filter);
    Genre getByID(Long id) throws GenreNotFoundException;
    Genre save(Genre genre);
    void deleteByID(Long id);
}
