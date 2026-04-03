package com.app.modules.user.model;

import com.app.core.model.BaseModel;
import com.app.core.security.rbac.Role;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.core.utils.validator.NumberValidator;
import com.app.core.utils.validator.StringValidator;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.exception.UserValidationException;
import com.app.modules.user.utils.RoleNameConverter;
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
@Table(name = "users")
public class User extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String hashedPassword;

    @Convert(converter = RoleNameConverter.class)
    @Column(nullable = false)
    private Role role = Role.USER;

    public User() {
        super(UserValidationException::new);
    }

    public User(
            RegisterUserDTO dto,
            PasswordHasher hasher
    ) {
        super(UserValidationException::new);
        Objects.requireNonNull(dto, "dto must not be null");

        this.firstname = NormalizeSanitizer.sanitize(dto.firstname());
        this.surname = NormalizeSanitizer.sanitize(dto.surname());
        this.lastname = NormalizeSanitizer.sanitize(dto.lastname());
        this.email = NormalizeSanitizer.normalize(dto.email());
        this.role = Role.USER;
        this.validate();

        this.setPassword(dto.password(), hasher);
    }

    public void setPassword(String rawPass, PasswordHasher hasher){
        Objects.requireNonNull(hasher, "password hasher must not be null");

        validateRawPassword(rawPass);
        this.hashedPassword = hashPassword(rawPass, hasher);
    }

    public boolean comparePassword(String rawPass, PasswordHasher hasher) {
        Objects.requireNonNull(rawPass, "password must not be null");
        Objects.requireNonNull(hasher, "password hasher must not be null");

        return hasher.matches(rawPass, this.hashedPassword);
    }

    private String hashPassword(String rawPass, PasswordHasher hasher) {
        Objects.requireNonNull(rawPass, "password must not be null");
        Objects.requireNonNull(hasher, "password hasher must not be null");

        return hasher.encode(rawPass);
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
        list.add(this.validateFirstname());
        list.add(this.validateLastname());
        list.add(this.validateSurname());
        list.add(this.validateEmail());
    }

    private void addStrictValidations(List<Map<String, String>> list) {
        list.add(this.validateId());
        list.add(this.validateHashedPassword());
    }

    private static final String ID_KEY = "id";
    private static final String HASHED_PASSWORD_KEY = "hashedPassword";
    private static final String FIRSTNAME_KEY = "firstname";
    private static final String LASTNAME_KEY = "lastname";
    private static final String SURNAME_KEY = "surname";
    private static final String EMAIL_KEY = "email";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$");


    private Map<String, String> validateId() {
        return new NumberValidator<>(ID_KEY, this.id)
                .notNull()
                .min(1L)
                .validate();
    }

    private Map<String, String> validateHashedPassword() {
        return new StringValidator(HASHED_PASSWORD_KEY, this.hashedPassword)
                .notNull()
                .validate();
    }

    private Map<String, String> validateFirstname() {
        return new StringValidator(FIRSTNAME_KEY, this.firstname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(100)
                .validate();
    }

    private Map<String, String> validateLastname() {
        return new StringValidator(LASTNAME_KEY, this.lastname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(100)
                .validate();
    }

    private Map<String, String> validateSurname() {
        return new StringValidator(SURNAME_KEY, this.surname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(100)
                .validate();
    }

    private Map<String, String> validateEmail() {
        return new StringValidator(EMAIL_KEY, this.email)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(100)
                .match(EMAIL_PATTERN)
                .validate();
    }

    private static final Pattern UPPERCASE_CONTAINS_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_CONTAINS_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_CONTAINS_PATTERN = Pattern.compile(".*\\d.*");

    private void validateRawPassword(String pass) throws UserValidationException {
        super.checkErrorsAndThrow(
                new StringValidator("password", pass)
                        .notNull()
                        .notBlank()
                        .minLength(8)
                        .match(UPPERCASE_CONTAINS_PATTERN, "must contain uppercase letter")
                        .match(LOWERCASE_CONTAINS_PATTERN, "must contain lowercase letter")
                        .match(DIGIT_CONTAINS_PATTERN, "must contain digit")
                        .validate());
    }
}
