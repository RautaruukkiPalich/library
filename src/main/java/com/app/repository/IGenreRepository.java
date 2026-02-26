package com.app.repository;

import java.util.List;

import com.app.exception.GenreNotFoundException;
import com.app.filter.GenreFilter;
import com.app.model.Genre;

public interface IGenreRepository {
    public List<Genre> getAll();
    public List<Genre> getAll(GenreFilter filter);
    public Genre getByID(Long id) throws GenreNotFoundException;
    public Genre save(Genre genre);
    public void deleteByID(Long id);
}
