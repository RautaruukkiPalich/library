package com.app.model;

import com.app.dto.BookDTO;
import com.app.exception.validation.BookValidationException;
import com.app.utils.map.MapUtils;
import com.app.utils.validator.StringValidator;
import com.app.utils.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class BookTests {

    private BookDTO validBookDTO;
    private Author validAuthor;
    private Genre validGenre;
    private Author invalidAuthor;
    private Genre invalidGenre;

    @BeforeEach
    void setUp() {
        validBookDTO = BookDTO.builder()
                .title("Matrix")
                .authorId(1L)
                .genreId(1L)
                .pubYear(1999)
                .isbn("978-5-127-12345-7")
                .pageCount(150)
                .isAvailable(true)
                .build();

        validAuthor = new Author(
                "123",
                "123",
                "123"
        );
        validAuthor.setId(1L);

        validGenre = new Genre(
                "123"
        );
        validGenre.setId(1L);

        invalidAuthor = new Author(
                "123",
                "123",
                "123"
        );

        invalidGenre = new Genre(
                "123"
        );

    }

    @Test
    void NewBook_onValidDtoReturnsNotNullObject() {
        Book book = new Book(validBookDTO, validAuthor, validGenre);

        assertThat(book)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getId()).isNull();
                    assertThat(a.getTitle()).isEqualTo(validBookDTO.title());
                    assertThat(a.getAuthor()).isEqualTo(validAuthor);
                    assertThat(a.getGenre()).isEqualTo(validGenre);
                    assertThat(a.getPubYear()).isEqualTo(validBookDTO.pubYear());
                    assertThat(a.getIsbn()).isEqualTo(validBookDTO.isbn());
                    assertThat(a.isAvailable()).isEqualTo(validBookDTO.isAvailable());
                    assertThat(a.getPageCount()).isEqualTo(validBookDTO.pageCount());
                    assertDoesNotThrow(a::validate);
                });
    }

    @Test
    void NewBook_onNullDtoThrowsNPE() {
        assertThatThrownBy(() -> new Book(null, validAuthor, validGenre))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void NewBook_onNullAuthorThrowsNPE() {
        assertThatThrownBy(() -> new Book(validBookDTO, null, validGenre))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void NewBook_onNullGenreThrowsNPE() {
        assertThatThrownBy(() -> new Book(validBookDTO, validAuthor, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void LoadedBook_onValidFieldsNoErrors() {
        Book book = new Book(validBookDTO, validAuthor, validGenre);
        book.setId(1L);
        book.setCreatedAt(OffsetDateTime.now());
        book.setUpdatedAt(OffsetDateTime.now());

        assertThat(book)
                .isNotNull()
                .satisfies(a -> {
                    assertDoesNotThrow(a::validateStrict);
                });
    }

    @Test
    void LoadedBook_onInvalidFieldsThrowException() {
        Book book = new Book(validBookDTO, validAuthor, validGenre);
        book.setId(0L);
        book.setCreatedAt(OffsetDateTime.now().plusDays(3));
        book.setUpdatedAt(OffsetDateTime.now().plusDays(3));

        var expectedErrors = Map.of(
//                "created_at", "date is too late",
//                "updated_at", "date is too late",
                "id", "cant be less than 1"
        );

        BookValidationException ex = assertThrows(
                BookValidationException.class,
                book::validateStrict
        );

        assertNotNull(ex);
        var actualErrors = ex.getErrorsMap();
        assertNotNull(actualErrors);
        assertFalse(actualErrors.isEmpty());
        assertTrue(MapUtils.allMatch(actualErrors, expectedErrors, String::contains));
    }

    @Test
    void ValidateGenre_throwsValidationException() {
        record ValidationTestCase(
                Book book,
                Map<String, String> expectedErrors
        ) {
        }

        final String ERR_NULL = Validator.ERROR_NULL;
        final String ERR_BLANK = StringValidator.ERROR_BLANK;
        final String ERR_SHORT_2 = String.format(StringValidator.ERROR_TOO_SHORT, 2);
        final String ERR_LONG_255 = String.format(StringValidator.ERROR_TOO_LONG, 255);

        final String TITLE = "title";
        final String AUTHOR = "author";
        final String GENRE = "genre";
        final String PUB_YEAR = "pubYear";
        final String ISBN = "isbn";
        final String IS_AVAILABLE = "isAvailable";
        final String PAGE_COUNT = "pageCount";

        final String BLANC_STRING = "";
        final String SHORT_STRING = "1";
        final String LONG_STRING = "1".repeat(260);
        final String VALID_STRING = "132";

        Author authorInvalidID = new Author(VALID_STRING, VALID_STRING, VALID_STRING);
        authorInvalidID.setId(0L);

        Genre genreInvalidID = new Genre(VALID_STRING);
        genreInvalidID.setId(0L);

        List<ValidationTestCase> tcs = List.of(
                new ValidationTestCase(
                        new Book(validBookDTO, invalidAuthor, validGenre),
                        Map.of(AUTHOR, "author id cant be null")
                ),
                new ValidationTestCase(
                        new Book(
                                validBookDTO,
                                authorInvalidID,
                                validGenre
                        ),
                        Map.of(AUTHOR, "author id cant be less than 1")
                ),
                new ValidationTestCase(
                        new Book(validBookDTO, validAuthor, invalidGenre),
                        Map.of(GENRE, "genre id cant be null")
                ),
                new ValidationTestCase(
                        new Book(
                                validBookDTO,
                                validAuthor,
                                genreInvalidID
                        ),
                        Map.of(GENRE, "genre id cant be less than 1")
                ),
                new ValidationTestCase(
                        new Book(
                                BLANC_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2000,
                                2000,
                                true

                        ),
                        Map.of(TITLE, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Book(
                                SHORT_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2000,
                                2000,
                                true

                        ),
                        Map.of(TITLE, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        new Book(
                                LONG_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2000,
                                2000,
                                true

                        ),
                        Map.of(TITLE, ERR_LONG_255)
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                null,
                                2000,
                                2000,
                                true

                        ),
                        Map.of(ISBN, ERR_NULL)
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "",
                                2000,
                                2000,
                                true

                        ),
                        Map.of(ISBN, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "123123",
                                2000,
                                2000,
                                true

                        ),
                        Map.of(ISBN, "invalid format. expected format: 978-5-127-12345-7")
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                0,
                                100,
                                true

                        ),
                        Map.of(PUB_YEAR, "cant be less than 1900")
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2100,
                                100,
                                true

                        ),
                        Map.of(PUB_YEAR, "cant be greater than 2040")
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2000,
                                0,
                                true

                        ),
                        Map.of(PAGE_COUNT, "cant be less than 1")
                ),
                new ValidationTestCase(
                        new Book(
                                VALID_STRING,
                                validAuthor,
                                validGenre,
                                "978-5-127-12345-7",
                                2000,
                                20001,
                                true

                        ),
                        Map.of(PAGE_COUNT, "cant be greater than 20000")
                )
        );

        tcs.forEach(tc -> {
            BookValidationException ex = assertThrows(
                    BookValidationException.class,
                    tc.book::validate
            );

            assertNotNull(ex);
            var actualErrors = ex.getErrorsMap();
            assertNotNull(actualErrors);
            assertFalse(actualErrors.isEmpty());
            assertTrue(MapUtils.anyMatch(actualErrors, tc.expectedErrors, String::contains), "321");
        });

    }
}
