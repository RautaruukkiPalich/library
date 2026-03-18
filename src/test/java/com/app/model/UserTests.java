package com.app.model;

import com.app.dto.UserDTO;
import com.app.exception.validation.UserValidationException;
import com.app.utils.map.MapUtils;
import com.app.utils.validator.Validator;
import com.app.utils.validator.StringValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserTests {

    private UserDTO validUserDTO;
    private IPasswordHasher mockHasher;
    private final String VALID_PASSWORD = "Password123";
    private final String HASHED_PASSWORD = "hashedPassword123";

    @BeforeEach
    void setUp() {
        validUserDTO = UserDTO.builder()
                .firstname("John")
                .surname("Doe")
                .lastname("Smith")
                .email("john.smith@example.com")
                .rawPassword(VALID_PASSWORD)
                .build();

        mockHasher = mock(IPasswordHasher.class);
        when(mockHasher.encode(any())).thenReturn(HASHED_PASSWORD);
        when(mockHasher.matches(any(), any())).thenReturn(true);
    }

    @Test
    void NewUser_onValidDtoAndHasherReturnsNotNullObject() {
        User user = new User(validUserDTO, mockHasher);

        assertThat(user)
                .isNotNull()
                .satisfies(u -> {
                    assertThat(u.getId()).isNull();
                    assertThat(u.getFirstname()).isEqualTo(validUserDTO.firstname());
                    assertThat(u.getSurname()).isEqualTo(validUserDTO.surname());
                    assertThat(u.getLastname()).isEqualTo(validUserDTO.lastname());
                    assertThat(u.getEmail()).isEqualTo(validUserDTO.email().toLowerCase().trim());
                    assertThat(u.getHashedPassword()).isEqualTo(HASHED_PASSWORD);
                    assertDoesNotThrow(u::validate);
                });

        verify(mockHasher).encode(VALID_PASSWORD);
    }

    @Test
    void NewUser_onNullDtoThrowsNPE() {
        assertThatThrownBy(() -> new User(null, mockHasher))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dto must not be null");
    }

    @Test
    void NewUser_onNullHasherThrowsNPE() {
        assertThatThrownBy(() -> new User(validUserDTO, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("password hasher must not be null");
    }

    @Test
    void comparePassword_onValidPasswordReturnsTrue() {
        User user = new User(validUserDTO, mockHasher);

        assertTrue(user.comparePassword(VALID_PASSWORD, mockHasher));

        verify(mockHasher).matches(VALID_PASSWORD, HASHED_PASSWORD);
    }

    @Test
    void comparePassword_onInvalidPasswordReturnsFalse() {
        when(mockHasher.matches(any(), any())).thenReturn(false);
        User user = new User(validUserDTO, mockHasher);

        assertFalse(user.comparePassword("WrongPassword", mockHasher));

        verify(mockHasher).matches("WrongPassword", HASHED_PASSWORD);
    }

    @Test
    void comparePassword_onNullPasswordThrowsNPE() {
        User user = new User(validUserDTO, mockHasher);

        assertThatThrownBy(() -> user.comparePassword(null, mockHasher))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("password must not be null");
    }

    @Test
    void comparePassword_onNullHasherThrowsNPE() {
        User user = new User(validUserDTO, mockHasher);

        assertThatThrownBy(() -> user.comparePassword(VALID_PASSWORD, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("password hasher must not be null");
    }

    @Test
    void LoadedUser_onValidFieldsNoErrors() {
        User user = new User(validUserDTO, mockHasher);
        user.setId(1L);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        assertDoesNotThrow(user::validateStrict);
    }

    @Test
    void LoadedUser_onInvalidFieldsThrowException() {
        User user = new User(validUserDTO, mockHasher);
        user.setId(0L);
        user.setCreatedAt(OffsetDateTime.now().plusDays(3));
        user.setUpdatedAt(OffsetDateTime.now().plusDays(3));

        var expectedErrors = Map.of(
                "createdAt", "date is too late",
                "updatedAt", "date is too late",
                "id", "cant be less than 1"
        );

        UserValidationException ex = assertThrows(
                UserValidationException.class,
                user::validateStrict
        );

        assertNotNull(ex);
        var actualErrors = ex.getErrorsMap();
        assertNotNull(actualErrors);
        assertFalse(actualErrors.isEmpty());
        assertTrue(MapUtils.allMatch(actualErrors, expectedErrors, String::contains));
    }

    @Test
    void constructor_sanitizesNames() {
        UserDTO dto = UserDTO.builder()
                .firstname("  John  Doe  ")
                .surname("  Smith  ")
                .lastname("  Johnson  ")
                .email("test@test.com")
                .rawPassword(VALID_PASSWORD)
                .build();

        User user = new User(dto, mockHasher);

        assertAll(
                () -> assertThat(user.getFirstname()).isEqualTo("John Doe"),
                () -> assertThat(user.getSurname()).isEqualTo("Smith"),
                () -> assertThat(user.getLastname()).isEqualTo("Johnson"),
                () -> assertThat(user.getEmail()).isEqualTo("test@test.com")
        );
    }

    @Test
    void constructor_normalizesEmail() {
        UserDTO dto = UserDTO.builder()
                .firstname("John")
                .surname("Smith")
                .lastname("Johnson")
                .email("  John.Smith@Example.COM  ")
                .rawPassword(VALID_PASSWORD)
                .build();

        User user = new User(dto, mockHasher);

        assertThat(user.getEmail()).isEqualTo("john.smith@example.com");
    }

    @Test
    void constructor_removesHtmlFromNames() {
        UserDTO dto = UserDTO.builder()
                .firstname("John<script>alert('xss')</script>")
                .surname("<b>Smith</b>")
                .lastname("Johnson&quot;")
                .email("test@test.com")
                .rawPassword(VALID_PASSWORD)
                .build();

        User user = new User(dto, mockHasher);

        assertAll(
                () -> assertThat(user.getFirstname()).isEqualTo("John&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;")
                        .doesNotContain("<script")
                        .contains("&lt;script"),
                () -> assertThat(user.getSurname()).isEqualTo("&lt;b&gt;Smith&lt;/b&gt;")
                        .doesNotContain("<b>")
                        .contains("&lt;b"),
                () -> assertThat(user.getLastname()).isEqualTo("Johnson&amp;quot;")
                        .doesNotContain("&quot")
                        .contains("&amp;quot")
        );
    }

    @TestFactory
    Stream<DynamicTest> ValidateUser_throwsValidationException() {
        record ValidationTestCase(
                String desc,
                UserDTO dto,
                Map<String, String> expectedErrors
        ) {
        }

        final String ERR_NULL = Validator.ERROR_NULL;
        final String ERR_BLANK = StringValidator.ERROR_BLANK;
        final String ERR_SHORT_2 = String.format(StringValidator.ERROR_TOO_SHORT, 2);
        final String ERR_SHORT_8 = String.format(StringValidator.ERROR_TOO_SHORT, 8);
        final String ERR_LONG_100 = String.format(StringValidator.ERROR_TOO_LONG, 100);
        final String ERR_INVALID_EMAIL_PATTERN = String.format(
                "invalid pattern. expected '%s'",
                "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$");

        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final String SURNAME = "surname";
        final String EMAIL = "email";
        final String PASSWORD = "password";

        final String VALID_STRING = "Valid";
        final String SHORT_STRING = "1";
        final String LONG_STRING = "1".repeat(150);
        final String BLANK_STRING = "   ";
        final String EMPTY_STRING = "";

        final String VALID_EMAIL = "test@test.com";
        final String INVALID_EMAIL_WITHOUT_DOMAIN = "test@test";
        final String INVALID_EMAIL_WITHOUT_AT = "test.test";
        final String INVALID_EMAIL_DOUBLE_AT = "test@test@test.com";

        List<ValidationTestCase> tcs = List.of(
                new ValidationTestCase(
                        "on null firstname expect error",
                        UserDTO.builder()
                                .firstname(null)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(FIRSTNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        "on blank firstname expect error",
                        UserDTO.builder()
                                .firstname(BLANK_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(FIRSTNAME, ERR_BLANK)
                ),
                new ValidationTestCase(
                        "on short firstname expect error",
                        UserDTO.builder()
                                .firstname(SHORT_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(FIRSTNAME, ERR_SHORT_2)
                ),
                new ValidationTestCase(
                        "on long firstname expect error",
                        UserDTO.builder()
                                .firstname(LONG_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(FIRSTNAME, ERR_LONG_100)
                ),

                new ValidationTestCase(
                        "on null surname expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(null)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(SURNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        "on blank surname expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(BLANK_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(SURNAME, ERR_BLANK)
                ),

                new ValidationTestCase(
                        "on null lastname expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(null)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(LASTNAME, ERR_NULL)
                ),
                new ValidationTestCase(
                        "on blank lastname expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(BLANK_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(LASTNAME, ERR_BLANK)
                ),

                new ValidationTestCase(
                        "on null email expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(null)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(EMAIL, ERR_NULL)
                ),
                new ValidationTestCase(
                        "on blank email expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(BLANK_STRING)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(EMAIL, ERR_BLANK)
                ),
                new ValidationTestCase(
                        "on invalid email without domain expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(INVALID_EMAIL_WITHOUT_DOMAIN)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(EMAIL, ERR_INVALID_EMAIL_PATTERN)
                ),
                new ValidationTestCase(
                        "on invalid email with double at expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(INVALID_EMAIL_DOUBLE_AT)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(EMAIL, ERR_INVALID_EMAIL_PATTERN)
                ),
                new ValidationTestCase(
                        "on invalid email without at expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(INVALID_EMAIL_WITHOUT_AT)
                                .rawPassword(VALID_PASSWORD)
                                .build(),
                        Map.of(EMAIL, ERR_INVALID_EMAIL_PATTERN)
                ),

                new ValidationTestCase(
                        "on null password expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(null)
                                .build(),
                        Map.of(PASSWORD, ERR_NULL)
                ),
                new ValidationTestCase(
                        "on blank password expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword(BLANK_STRING)
                                .build(),
                        Map.of(PASSWORD, ERR_BLANK)
                ),
                new ValidationTestCase(
                        "on short password expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword("Pass1")
                                .build(),
                        Map.of(PASSWORD, ERR_SHORT_8)
                ),
                new ValidationTestCase(
                        "on password without uppercase expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword("password123")
                                .build(),
                        Map.of(PASSWORD, "invalid pattern. must contain uppercase letter")
                ),
                new ValidationTestCase(
                        "on password without lowercase expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword("PASSWORD123")
                                .build(),
                        Map.of(PASSWORD, "invalid pattern. must contain lowercase letter")
                ),
                new ValidationTestCase(
                        "on password without digit expect error",
                        UserDTO.builder()
                                .firstname(VALID_STRING)
                                .surname(VALID_STRING)
                                .lastname(VALID_STRING)
                                .email(VALID_EMAIL)
                                .rawPassword("Password")
                                .build(),
                        Map.of(PASSWORD, "invalid pattern. must contain digit")
                ),

                new ValidationTestCase(
                        "on multiple invalid fields expect multiple errors",
                        UserDTO.builder()
                                .firstname(SHORT_STRING)
                                .surname(BLANK_STRING)
                                .lastname(null)
                                .email("invalid")
                                .rawPassword("weak")
                                .build(),
                        Map.of(
                                FIRSTNAME, ERR_SHORT_2,
                                SURNAME, ERR_BLANK,
                                LASTNAME, ERR_NULL,
                                EMAIL, "invalid pattern",
                                PASSWORD, ERR_SHORT_8
                        )
                )
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc, () -> {
            UserValidationException ex = assertThrows(
                    UserValidationException.class,
                    () -> new User(tc.dto, mockHasher)
            );

            assertNotNull(ex);
            var actualErrors = ex.getErrorsMap();
            assertNotNull(actualErrors);
            assertFalse(actualErrors.isEmpty());

            assertTrue(MapUtils.anyMatch(actualErrors, tc.expectedErrors, String::contains),
                    () -> String.format("\nactual: %s\nexpected: %s\n",
                            actualErrors, tc.expectedErrors));

            verify(mockHasher, never()).encode(any());
        }));
    }
}