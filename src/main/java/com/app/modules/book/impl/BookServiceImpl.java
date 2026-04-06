package com.app.modules.book.impl;

import com.app.modules.author.model.Author;
import com.app.modules.author.repository.AuthorRepository;
import com.app.modules.book.api.BookService;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookValidationException;
import com.app.modules.book.mapper.BookMapper;
import com.app.modules.book.model.Book;
import com.app.modules.book.repository.BookRepository;
import com.app.modules.genre.model.Genre;
import com.app.modules.genre.repository.GenreRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Service
@Transactional
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final GenreRepository genreRepo;

    @Transactional(readOnly = true)
    public List<BookDTO> getAll() {
        return BookMapper.toListDTO(this.bookRepo.findAll());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getAll(@NonNull BookFilter filter) {
        return BookMapper.toListDTO(this.bookRepo.findAll(filter));
    }

    @Transactional(readOnly = true)
    public BookDTO getByID(@NonNull Long id) {
        return BookMapper.toDTO(this.getBookByIDInternal(id));
    }

    public Long add(@NonNull BookDTO dto) {
        this.preflightValidateDTO(dto);

        Author author = this.authorRepo.getById(dto.authorId());
        Genre genre = this.genreRepo.getById(dto.genreId());

        Book book = new Book(dto, author, genre);
        book.validate();

        Book savedBook = this.bookRepo.save(book);
        savedBook.validateStrict();

        return savedBook.getId();
    }

    public void delete(@NonNull Long id) {
        Book book = this.bookRepo.getById(id);
        this.bookRepo.delete(book);
    }

    public void putByID(@NonNull Long id, @NonNull BookDTO dto) {
        this.preflightValidateDTO(dto);

        Author author = this.getAuthorByIDInternal(dto.authorId());
        Genre genre = this.getGenreByIDInternal(dto.genreId());
        Book book = this.getBookByIDInternal(id);

        book.setTitle(dto.title());
        book.setAuthor(author);
        book.setGenre(genre);
        book.setPubYear(dto.pubYear());
        book.setIsbn(dto.isbn());
        book.setAvailable(dto.isAvailable());
        book.setPageCount(dto.pageCount());

        book.validateStrict();
        this.bookRepo.save(book);
    }

    @Override
    public void patchByID(@NonNull Long id, @NonNull BookDTO dto) {

        Book book = this.getBookByIDInternal(id);
        if (dto.title() != null) {
            book.setTitle(dto.title());
        }
        if (dto.authorId() != null) {
            book.setAuthor(this.getAuthorByIDInternal(dto.authorId()));
        }
        if (dto.genreId() != null) {
            book.setGenre(this.getGenreByIDInternal(dto.genreId()));
        }
        if (dto.pubYear() != null) {
            book.setPubYear(dto.pubYear());
        }
        if (dto.isbn() != null) {
            book.setIsbn(dto.isbn());
        }
        if (dto.isAvailable() != null) {
            book.setAvailable(dto.isAvailable());
        }
        if (dto.pageCount() != null) {
            book.setPageCount(dto.pageCount());
        }

        book.validateStrict();
        this.bookRepo.save(book);
    }

    private void preflightValidateDTO(@NonNull BookDTO dto) throws BookValidationException {
        if (dto.authorId() == null) {
            throw new BookValidationException("authorId", "is required");
        }
        if (dto.genreId() == null) {
            throw new BookValidationException("genreId", "is required");
        }
    }

    private Book getBookByIDInternal(@NonNull Long id) {
        return this.bookRepo.getById(id);
    }

    private Genre getGenreByIDInternal(@NonNull Long id) {
        return this.genreRepo.getById(id);
    }

    private Author getAuthorByIDInternal(@NonNull Long id) {
        return this.authorRepo.getById(id);
    }
}
