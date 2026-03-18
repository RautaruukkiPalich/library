package com.app.modules.book.repository;

import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import com.app.modules.book.model.Book;

import java.util.List;

public interface BookRepository {
    List<Book> getAll();

    List<Book> getAll(BookFilter filter);

    Book getByID(Long id) throws BookNotFoundException;

    Book save(Book book);

    void delete(Book book);
}
