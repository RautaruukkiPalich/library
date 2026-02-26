package com.app.repository;

import com.app.exception.GenreNotFoundException;
import com.app.filter.GenreFilter;
import com.app.model.Genre;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresGenreRepository implements IGenreRepository{
    @Override
    public List<Genre> getAll() {
        return List.of();
    }

    @Override
    public List<Genre> getAll(GenreFilter filter) {
        return List.of();
    }

    @Override
    public Genre getByID(Long id) throws GenreNotFoundException {
        return null;
    }

    @Override
    public Genre save(Genre genre) {
        return null;
    }

    @Override
    public void deleteByID(Long id) {

    }
}
