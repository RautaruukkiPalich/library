package com.app.model;

import com.app.core.utils.validator.StringValidator;
import com.app.core.utils.validator.Validator;
import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.exception.GenreValidationException;
import com.app.modules.genre.model.Genre;
import com.app.utils.map.MapUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class GenreTests {

    private GenreDTO validGenreDTO;

    @BeforeEach
    void setUp() {
        validGenreDTO = GenreDTO.builder()
                .name("lyric")
                .build();
    }

    @Test
    void NewGenre_onValidDtoReturnsNotNullObject() {
        Genre genre = new Genre(validGenreDTO);

        assertThat(genre)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getId()).isNull();
                    assertThat(a.getName()).isEqualTo(validGenreDTO.name());
                    assertDoesNotThrow(a::validate);
                });
    }

    @Test
    void LoadedGenre_onValidFieldsNoErrors() {
        Genre genre = new Genre(validGenreDTO);
        genre.setId(1L);
        genre.setCreatedAt(OffsetDateTime.now());
        genre.setUpdatedAt(OffsetDateTime.now());

        assertThat(genre)
                .isNotNull()
                .satisfies(a -> {
                    assertDoesNotThrow(a::validateStrict);
                });
    }

    @Test
    void LoadedGenre_onInvalidFieldsThrowException() {
        Genre genre = new Genre(validGenreDTO);
        genre.setId(0L);
        genre.setCreatedAt(OffsetDateTime.now().plusDays(3));
        genre.setUpdatedAt(OffsetDateTime.now().plusDays(3));

        var expectedErrors = Map.of(
                "createdAt", "date is too late",
                "updatedAt", "date is too late",
                "id", "must not be less than 1"
        );

        GenreValidationException ex = assertThrows(
                GenreValidationException.class,
                genre::validateStrict
        );

        assertNotNull(ex);
        var actualErrors = ex.getErrorsMap();
        assertNotNull(actualErrors);
        assertFalse(actualErrors.isEmpty());
        assertTrue(MapUtils.allMatch(actualErrors, expectedErrors, String::contains));
    }

    @Test
    void NewGenre_onNullDtoThrowsNPE() {
        assertThatThrownBy(() -> new Genre((GenreDTO) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ValidateGenre_throwsValidationException() {
        record ValidationTestCase(
                Genre genre,
                Map<String, String> expectedErrors
        ) {
        }

        final String ERR_NULL = Validator.ERROR_NULL;
        final String ERR_BLANK = StringValidator.ERROR_BLANK;
        final String ERR_SHORT_2 = String.format(StringValidator.ERROR_TOO_SHORT, 2);
        final String ERR_LONG_255 = String.format(StringValidator.ERROR_TOO_LONG, 255);

        final String NAME = "name";

        final String BLANC_STRING = "";
        final String SHORT_STRING = "1";
        final String LONG_STRING = "1".repeat(260);

        List<ValidationTestCase> tcs = List.of(
                new ValidationTestCase(
                        new Genre(),
                        Map.of(NAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        new Genre(BLANC_STRING),
                        Map.of(NAME, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Genre(SHORT_STRING),
                        Map.of(NAME, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        new Genre(LONG_STRING),
                        Map.of(NAME, ERR_LONG_255)
                )
        );

        tcs.forEach(tc -> {
            GenreValidationException ex = assertThrows(
                    GenreValidationException.class,
                    tc.genre::validate
            );

            assertNotNull(ex);
            var actualErrors = ex.getErrorsMap();
            assertNotNull(actualErrors);
            assertFalse(actualErrors.isEmpty());
            assertTrue(MapUtils.anyMatch(actualErrors, tc.expectedErrors, String::contains));
        });

    }
}
