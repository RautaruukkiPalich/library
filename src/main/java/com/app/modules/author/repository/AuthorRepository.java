package com.app.modules.author.repository;

import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.exception.AuthorNotFoundException;
import com.app.modules.author.model.Author;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<Author> findAll();

    List<Author> findAll(@NonNull AuthorFilter filter);

    Optional<Author> findById(@NonNull Long id);

    Author getById(@NonNull Long id) throws AuthorNotFoundException;

    Author save(@NonNull Author author);

    void delete(@NonNull Author author);
}
