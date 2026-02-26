package com.app.repository;

import java.util.List;

import com.app.exception.BookNotFoundException;
import com.app.filter.BookFilter;
import com.app.model.Book;

public interface IBookRepository {
    List<Book> getAll();
    List<Book> getAll(BookFilter filter);
    Book getByID(Long id) throws BookNotFoundException;
    Book save(Book book);
    void deleteByID(Long id);
}
