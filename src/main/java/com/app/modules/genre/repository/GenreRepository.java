package com.app.modules.genre.repository;

import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.exception.GenreNotFoundException;
import com.app.modules.genre.model.Genre;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAll(@NonNull GenreFilter filter);

    Genre getById(@NonNull Long id) throws GenreNotFoundException;

    Optional<Genre> findById(@NonNull Long id);

    Genre save(@NonNull Genre genre);

    void delete(@NonNull Genre genre);
}
