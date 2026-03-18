package com.app.modules.book.api;

import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import jakarta.validation.ValidationException;

import java.util.List;

public interface BookService {
    List<BookDTO> getAll();

    List<BookDTO> getAll(BookFilter filter);

    BookDTO getByID(Long id) throws BookNotFoundException;

    void putByID(Long id, BookDTO book) throws BookNotFoundException;

    void patchByID(Long id, BookDTO book) throws BookNotFoundException;

    Long add(BookDTO book) throws ValidationException;

    void delete(Long id) throws BookNotFoundException;
}
