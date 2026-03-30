package com.app.modules.genre.repository;

import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.exception.GenreNotFoundException;
import com.app.modules.genre.model.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> getAll();

    List<Genre> getAll(GenreFilter filter);

    Genre getByID(Long id) throws GenreNotFoundException;

    Genre save(Genre genre);

    void delete(Genre genre);
}
