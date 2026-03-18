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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Primary
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final GenreRepository genreRepo;

    public BookServiceImpl(
            BookRepository bookRepo,
            AuthorRepository authorRepo,
            GenreRepository genreRepo
    ) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAll() {
        return BookMapper.toListDTO(this.bookRepo.getAll());
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAll(BookFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");
        return BookMapper.toListDTO(this.bookRepo.getAll(filter));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getByID(Long id) {
        return BookMapper.toDTO(this.getBookByIDInternal(id));
    }

    @Override
    @Transactional
    public Long add(BookDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        this.preflightValidateDTO(dto);

        Author author = this.authorRepo.getByID(dto.authorId());
        Genre genre = this.genreRepo.getByID(dto.genreId());

        Book book = new Book(dto, author, genre);
        book.validate();

        Book savedBook = this.bookRepo.save(book);
        savedBook.validateStrict();

        return savedBook.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        Book book = this.bookRepo.getByID(id);
        this.bookRepo.delete(book);
    }

    @Override
    @Transactional
    public void putByID(Long id, BookDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

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
    @Transactional
    public void patchByID(Long id, BookDTO dto) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(dto, "dto must not be null");

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

    private void preflightValidateDTO(BookDTO dto) throws BookValidationException {
        if (dto.authorId() == null) {
            throw new BookValidationException("authorId", "is required");
        }
        if (dto.genreId() == null) {
            throw new BookValidationException("genreId", "is required");
        }
    }

    private Book getBookByIDInternal(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return this.bookRepo.getByID(id);
    }

    private Genre getGenreByIDInternal(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return this.genreRepo.getByID(id);
    }

    private Author getAuthorByIDInternal(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return this.authorRepo.getByID(id);
    }
}
