package com.app.modules.book.repository;

import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import com.app.modules.book.model.Book;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findAll();

    List<Book> findAll(@NonNull BookFilter filter);

    Book getById(@NonNull Long id) throws BookNotFoundException;

    Optional<Book> findById(@NonNull Long id);

    Book save(@NonNull Book book);

    void delete(@NonNull Book book);
}
