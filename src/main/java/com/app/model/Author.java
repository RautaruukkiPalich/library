package com.app.model;

import com.app.dto.AuthorDTO;
import com.app.exception.validation.AuthorValidationException;
import com.app.model.mixin.DateMixin;
import com.app.utils.MapMerger;
import com.app.utils.validator.LongValidator;
import com.app.utils.validator.StringValidator;
import com.app.utils.validator.Validator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

@Getter
@Setter
@Entity
@Table(name = "authors")
public class Author extends DateMixin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 255)
    private String firstname;

    @Column(nullable = false, length = 255)
    private String lastname;

    @Column(nullable = false, length = 255)
    private String surname;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;

    private static final String ID_KEY = "id";
    private static final String FIRSTNAME_KEY = "firstname";
    private static final String LASTNAME_KEY = "lastname";
    private static final String SURNAME_KEY = "surname";

    private static final BinaryOperator<String> MERGE_FUNC = (v1, v2) -> v1 + "; " + v2;


    public Author() {
        super();
    }

    public Author(
            String firstname,
            String lastname,
            String surname
    ) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
        this.surname = surname;
    }

    public Author(AuthorDTO dto) {
        super();
        Objects.requireNonNull(dto, "dto must not be null");

        this.firstname = dto.firstname();
        this.lastname = dto.lastname();
        this.surname = dto.surname();
    }

    public void validate() throws AuthorValidationException {
        checkValidationErrors(
                this.validateFirstname(),
                this.validateLastname(),
                this.validateSurname()
        );
    }

    public void validateStrict() throws AuthorValidationException {
        checkValidationErrors(
                this.validateId(),
                this.validateFirstname(),
                this.validateLastname(),
                this.validateSurname()
        );
    }

    @SafeVarargs
    private void checkValidationErrors(Map<String, String>... maps) throws AuthorValidationException {
        var errors = new MapMerger<>(maps)
                .withMergeFunc(MERGE_FUNC)
                .merge();

        if (errors != null && !errors.isEmpty()) {
            throw new AuthorValidationException(errors);
        }
    }

    private Map<String, String> validateId() {
        return new LongValidator(ID_KEY, this.id)
                .notNull()
                .min(1L)
                .max(null)
                .validate();
//        return Validator.validateLong(ID_KEY, this.id, 1L, null);
    }

    private Map<String, String> validateFirstname() {
        return new StringValidator(FIRSTNAME_KEY, this.firstname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
//        return Validator.validateString(FIRSTNAME_KEY, this.firstname, 2, 255);
    }

    private Map<String, String> validateLastname() {
        return new StringValidator(LASTNAME_KEY, this.lastname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
//        return Validator.validateString(LASTNAME_KEY, this.lastname, 2, 255);
    }

    private Map<String, String> validateSurname() {
        return new StringValidator(SURNAME_KEY, this.surname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
//        return Validator.validateString(SURNAME_KEY, this.surname, 2, 255);
    }
}