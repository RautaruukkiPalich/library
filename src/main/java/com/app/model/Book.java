package com.app.model;

import com.app.dto.BookDTO;
import com.app.exception.validation.BookValidationException;
import com.app.model.mixin.DateMixin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book extends DateMixin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "pub_year")
    private Integer pubYear;

    @Column(length = 20)
    private String isbn;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "page_count")
    private Integer pageCount;

    private static final Pattern ISBN_PATTERN = 
        Pattern.compile("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,6}-\\d$");

    public Book() {
    }

    public Book(
            String title,
            Author author,
            Genre genre,
            String isbn,
            int pubYear,
            int pageCount,
            boolean isAvailable
    ) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.pubYear = pubYear;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
        this.pageCount = pageCount;
    }

    public Book(BookDTO dto, Author author, Genre genre) {
        Objects.requireNonNull(dto, "dto cant be null");
        Objects.requireNonNull(author, "author cant be null");
        Objects.requireNonNull(genre, "genre cant be null");

        this.title = dto.title();
        this.author = author;
        this.genre = genre;
        this.pubYear = dto.pubYear();
        this.isbn = dto.isbn();
        this.isAvailable = dto.isAvailable();
        this.pageCount = dto.pageCount();
    }

    public void validate() throws BookValidationException {
        this.validateTitle();
        this.validateAuthor();
        this.validateGenre();
        this.validatePageCount();
        this.validatePubYear();
        this.validateIsbn();
    }

    public void validateStrict() throws BookValidationException {
        if (this.getId() <= 0) {
            throw new BookValidationException("id", "id cant be less than 1");
        }
        this.validate();
    }

    public void setAvailable() {
        this.isAvailable = true;
    }

    public void setBorrow() {
        this.isAvailable = false;
    }

    private void validateTitle() throws BookValidationException {
        if (this.title == null) {
            throw new BookValidationException("title", "cant be null");
        }
        if (this.title.isBlank()) {
            throw new BookValidationException("title", "cant be blank");
        }
        if (this.title.length() < 2) {
            throw new BookValidationException("title", "cant be shorter 2 characters");
        }
        if (this.title.length() > 255) {
            throw new BookValidationException("title", "cant be longer 255 characters");
        }
    }

    private void validateAuthor() throws BookValidationException {
        if (this.author == null) {
            throw new BookValidationException("author", "cant be null");
        }
        if (this.author.getId() <= 0) {
            throw new BookValidationException("author", "author id cant be less than 1");
        }
    }

    private void validateGenre() throws BookValidationException {
        if (this.genre == null) {
            throw new BookValidationException("genre", "cant be null");
        }
        if (this.genre.getId() <= 0) {
            throw new BookValidationException("genre", "author id cant be less than 1");
        }
    }

    private void validateIsbn() throws BookValidationException {
        if (this.isbn == null) {
            throw new BookValidationException("isbn", "cant be null");
        }
        if (this.isbn.isBlank()) {
            throw new BookValidationException("isbn", "cant be blank");
        }
        if (!ISBN_PATTERN.matcher(this.isbn).matches()) {
            throw new BookValidationException("isbn", "invalid format. expected format: 978-5-127-12345-7");
        }
    }

    private void validatePageCount() throws BookValidationException {
        if (this.pageCount == null) {
            throw new BookValidationException("pageCount", "cant be null");
        }
        if (this.pageCount < 1) {
            throw new BookValidationException("pageCount", "cant be less than 1");
        }
        if (this.pageCount > 20000) {
            throw new BookValidationException("pageCount", "cant be greater than 20000");
        }
    }

    private void validatePubYear() throws BookValidationException {
        if (this.pubYear == null) {
            throw new BookValidationException("pubYear", "cant be null");
        }
        if (this.pubYear < 1900) {
            throw new BookValidationException("pubYear", "cant be less than 1900");
        }
        if (this.pubYear > 2040) {
            throw new BookValidationException("pubYear", "cant be greater than 2040");
        }
    }
}
