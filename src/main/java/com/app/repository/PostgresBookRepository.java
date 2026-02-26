package com.app.repository;

import com.app.exception.BookNotFoundException;
import com.app.filter.BookFilter;
import com.app.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresBookRepository implements IBookRepository {
    @Override
    public List<Book> getAll() {
        return List.of();
    }

    @Override
    public List<Book> getAll(BookFilter filter) {
        return List.of();
    }

    @Override
    public Book getByID(Long id) throws BookNotFoundException {
        return null;
    }

    @Override
    public Book save(Book book) {
        return null;
    }

    @Override
    public void deleteByID(Long id) {

    }
}
