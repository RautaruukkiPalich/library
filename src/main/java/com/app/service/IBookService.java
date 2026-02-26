package com.app.service;

import com.app.model.Book;

import jakarta.validation.ValidationException;

import java.util.List;

import com.app.dto.BookDTO;
import com.app.filter.BookFilter;
import com.app.exception.BookNotFoundException;

public interface IBookService {
    public List<Book> GetAll();
    public List<Book> GetAll(BookFilter filter);
    public Book GetByID(Long id) throws BookNotFoundException;
    public void PutByID(Long id, BookDTO book) throws BookNotFoundException;
    public void PatchByID(Long id, BookDTO book) throws BookNotFoundException;
    public void Add(BookDTO book) throws ValidationException;;
    public void Delete(Long id) throws BookNotFoundException;
}
