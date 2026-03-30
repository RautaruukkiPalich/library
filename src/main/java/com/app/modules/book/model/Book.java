package com.app.modules.book.model;

import com.app.core.model.BaseModel;
import com.app.core.utils.validator.NumberValidator;
import com.app.core.utils.validator.ObjectValidator;
import com.app.core.utils.validator.StringValidator;
import com.app.modules.author.model.Author;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.exception.BookValidationException;
import com.app.modules.genre.model.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book extends BaseModel {
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

    public Book() {
        super(BookValidationException::new);
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
        super(BookValidationException::new);
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.pubYear = pubYear;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
        this.pageCount = pageCount;
    }

    public Book(BookDTO dto, Author author, Genre genre) {
        super(BookValidationException::new);
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

    public void setAvailable() {
        this.isAvailable = true;
    }

    public void setBorrow() {
        this.isAvailable = false;
    }

    @Override
    protected final List<Map<String, String>> validateBaseFields() {
        List<Map<String, String>> list = new ArrayList<>(super.validateBaseFields());
        addBaseValidations(list);
        return list;
    }

    @Override
    protected final List<Map<String, String>> validateStrictFields() {
        List<Map<String, String>> list = new ArrayList<>(super.validateStrictFields());
        addBaseValidations(list);
        addStrictValidations(list);
        return list;
    }

    private void addBaseValidations(List<Map<String, String>> list) {
        list.add(this.validateTitle());
        list.add(this.validateAuthor());
        list.add(this.validateGenre());
        list.add(this.validateIsbn());
        list.add(this.validatePubYear());
        list.add(this.validatePageCount());
    }

    private void addStrictValidations(List<Map<String, String>> list) {
        list.add(this.validateId());
    }

    private static final String ID_KEY = "id";
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String GENRE_KEY = "genre";
    private static final String ISBN_KEY = "isbn";
    private static final String PUB_YEAR_KEY = "pubYear";
    private static final String PAGE_COUNT_KEY = "pageCount";


    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,6}-\\d$");

    private Map<String, String> validateId() {
        return new NumberValidator<>(ID_KEY, this.id)
                .notNull()
                .min(1L)
                .validate();
    }

    private Map<String, String> validateTitle() {
        return new StringValidator(TITLE_KEY, this.title)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
    }

    private Map<String, String> validateAuthor() {
        return new ObjectValidator<>(AUTHOR_KEY, this.author)
                .notNull()
                .validateNumber(
                        Author::getId,
                        ID_KEY,
                        v -> v.notNull().min(1L)
                )
                .validate();
    }

    private Map<String, String> validateGenre() {
        return new ObjectValidator<>(GENRE_KEY, this.genre)
                .notNull()
                .validateNumber(
                        Genre::getId,
                        ID_KEY,
                        v -> v.notNull().min(1L)
                )
                .validate();
    }

    private Map<String, String> validateIsbn() {
        return new StringValidator(ISBN_KEY, this.isbn)
                .notNull()
                .notBlank()
                .minLength(10)
//                .maxLength(13)
                .match(ISBN_PATTERN)
                .validate();
    }

    private Map<String, String> validatePageCount() {
        return new NumberValidator<>(PAGE_COUNT_KEY, this.pageCount)
                .notNull()
                .min(1)
                .max(20000)
                .validate();
    }

    private Map<String, String> validatePubYear() {
        return new NumberValidator<>(PUB_YEAR_KEY, this.pubYear)
                .notNull()
                .min(1900)
                .max(2040)
                .validate();
    }
}
