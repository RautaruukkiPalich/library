package com.app.model;

import com.app.core.utils.validator.StringValidator;
import com.app.core.utils.validator.Validator;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.exception.AuthorValidationException;
import com.app.modules.author.model.Author;
import com.app.utils.map.MapUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class AuthorTests {

    private AuthorDTO validAuthorDTO;

    @BeforeEach
    void setUp() {
        validAuthorDTO = AuthorDTO.builder()
                .firstname("Alexander")
                .lastname("Pushkin")
                .surname("Sergeevich")
                .build();
    }

    @Test
    void NewAuthor_onValidDtoReturnsNotNullObject() {
        Author author = new Author(validAuthorDTO);

        assertThat(author)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getId()).isNull();
                    assertThat(a.getFirstname()).isEqualTo(validAuthorDTO.firstname());
                    assertThat(a.getLastname()).isEqualTo(validAuthorDTO.lastname());
                    assertThat(a.getSurname()).isEqualTo(validAuthorDTO.surname());
                    assertDoesNotThrow(a::validate);
                });
    }

    @Test
    void NewAuthor_onNullDtoThrowsNPE() {
        assertThatThrownBy(() -> new Author(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ValidateAuthor_throwsValidationException() {
        record ValidationTestCase(
                Author author,
                Map<String, String> expectedErrors
        ) {
        }

        final String ERR_NULL = Validator.ERROR_NULL;
        final String ERR_BLANK = StringValidator.ERROR_BLANK;
        final String ERR_SHORT_2 = String.format(StringValidator.ERROR_TOO_SHORT, 2);
        final String ERR_LONG_255 = String.format(StringValidator.ERROR_TOO_LONG, 255);

        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final String SURNAME = "surname";

        final String BLANC_STRING = "";
        final String SHORT_STRING = "1";
        final String LONG_STRING = "1".repeat(260);
        final String VALID_STRING = "132";

        List<ValidationTestCase> tcs = List.of(
                new ValidationTestCase(
                        new Author(null, VALID_STRING, VALID_STRING),
                        Map.of(FIRSTNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        new Author(BLANC_STRING, VALID_STRING, VALID_STRING),
                        Map.of(FIRSTNAME, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Author(SHORT_STRING, VALID_STRING, VALID_STRING),
                        Map.of(FIRSTNAME, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        new Author(LONG_STRING, VALID_STRING, VALID_STRING),
                        Map.of(FIRSTNAME, ERR_LONG_255)
                ),

                new ValidationTestCase(
                        new Author(VALID_STRING, null, VALID_STRING),
                        Map.of(LASTNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, BLANC_STRING, VALID_STRING),
                        Map.of(LASTNAME, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, SHORT_STRING, VALID_STRING),
                        Map.of(LASTNAME, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, LONG_STRING, VALID_STRING),
                        Map.of(LASTNAME, ERR_LONG_255)
                ),

                new ValidationTestCase(
                        new Author(VALID_STRING, VALID_STRING, null),
                        Map.of(SURNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, VALID_STRING, BLANC_STRING),
                        Map.of(SURNAME, ERR_BLANK)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, VALID_STRING, SHORT_STRING),
                        Map.of(SURNAME, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        new Author(VALID_STRING, VALID_STRING, LONG_STRING),
                        Map.of(SURNAME, ERR_LONG_255)
                ),
                new ValidationTestCase(
                        new Author(BLANC_STRING, BLANC_STRING, BLANC_STRING),
                        Map.of(
                                FIRSTNAME, ERR_BLANK,
                                LASTNAME, ERR_BLANK,
                                SURNAME, ERR_BLANK
                        )
                ),
                new ValidationTestCase(
                        new Author(SHORT_STRING, null, LONG_STRING),
                        Map.of(
                                FIRSTNAME, ERR_SHORT_2,
                                LASTNAME, ERR_NULL,
                                SURNAME, ERR_LONG_255
                        )
                )

        );

        tcs.forEach(tc -> {
            AuthorValidationException ex = assertThrows(
                    AuthorValidationException.class,
                    tc.author::validate
            );

            assertNotNull(ex);
            var actualErrors = ex.getErrorsMap();
            assertNotNull(actualErrors);
            assertFalse(actualErrors.isEmpty());
            assertTrue(MapUtils.anyMatch(actualErrors, tc.expectedErrors, String::contains));
        });

    }
}
