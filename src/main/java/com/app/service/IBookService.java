package com.app.service;

import com.app.model.Book;

import jakarta.validation.ValidationException;

import java.util.List;

import com.app.dto.BookDTO;
import com.app.filter.BookFilter;
import com.app.exception.notfound.BookNotFoundException;

public interface IBookService {
    List<Book> getAll();
    List<Book> getAll(BookFilter filter);
    Book getByID(Long id) throws BookNotFoundException;
    void putByID(Long id, BookDTO book) throws BookNotFoundException;
    void patchByID(Long id, BookDTO book) throws BookNotFoundException;
    Long add(BookDTO book) throws ValidationException;;
    void delete(Long id) throws BookNotFoundException;
}
