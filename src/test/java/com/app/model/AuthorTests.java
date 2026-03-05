package com.app.model;

import com.app.dto.AuthorDTO;
import com.app.exception.validation.AuthorValidationException;
import com.app.utils.MapUtils;
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

        List<ValidationTestCase> tcs = List.of(
                new ValidationTestCase(
                        new Author(null, "321", "321"),
                        Map.of("firstname", "cant be null")
                ),
                new ValidationTestCase(
                        new Author("", "321", "321"),
                        Map.of("firstname", "cant be blank")
                ),
                new ValidationTestCase(
                        new Author("1", "321", "321"),
                        Map.of("firstname", "cant be shorter 2 characters")
                ),
                new ValidationTestCase(
                        new Author("a".repeat(260), "321", "321"),
                        Map.of("firstname", "cant be longer 255 characters")
                ),

                new ValidationTestCase(
                        new Author("132", null, "321"),
                        Map.of("lastname", "cant be null")
                ),
                new ValidationTestCase(
                        new Author("132", "", "321"),
                        Map.of("lastname", "cant be blank")
                ),
                new ValidationTestCase(
                        new Author("132", "1", "321"),
                        Map.of("lastname", "cant be shorter 2 characters")
                ),
                new ValidationTestCase(
                        new Author("321", "a".repeat(260), "321"),
                        Map.of("lastname", "cant be longer 255 characters")
                ),

                new ValidationTestCase(
                        new Author("132", "123", null),
                        Map.of("surname", "cant be null")
                ),
                new ValidationTestCase(
                        new Author("132", "132", ""),
                        Map.of("surname", "cant be blank")
                ),
                new ValidationTestCase(
                        new Author("132", "123", "1"),
                        Map.of("surname", "cant be shorter 2 characters")
                ),
                new ValidationTestCase(
                        new Author("321", "321", "a".repeat(260)),
                        Map.of("surname", "cant be longer 255 characters")
                ),
                new ValidationTestCase(
                        new Author("", "", ""),
                        Map.of(
                                "firstname", "cant be blank",
                                "lastname", "cant be blank",
                                "surname", "cant be blank"
                        )
                ),
                new ValidationTestCase(
                        new Author("1", null, "a".repeat(260)),
                        Map.of(
                                "firstname", "cant be shorter 2 characters",
                                "lastname", "cant be null",
                                "surname", "cant be longer 255 characters"
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
            assertTrue(MapUtils.anyMatch(actualErrors, tc.expectedErrors));
        });

    }
}
