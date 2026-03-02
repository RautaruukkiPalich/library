package com.app.service;

import com.app.dto.BookDTO;
import com.app.exception.notfound.BookNotFoundException;
import com.app.filter.BookFilter;
import jakarta.validation.ValidationException;

import java.util.List;

public interface IBookService {
    List<BookDTO> getAll();

    List<BookDTO> getAll(BookFilter filter);

    BookDTO getByID(Long id) throws BookNotFoundException;

    void putByID(Long id, BookDTO book) throws BookNotFoundException;

    void patchByID(Long id, BookDTO book) throws BookNotFoundException;

    Long add(BookDTO book) throws ValidationException;

    void delete(Long id) throws BookNotFoundException;
}
