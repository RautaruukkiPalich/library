package com.app.model;

import com.app.dto.AuthorDTO;
import com.app.exception.validation.AuthorValidationException;
import com.app.model.mixin.DateMixin;
import com.app.utils.mapUtils.MapUtils;
import com.app.utils.validator.Validator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        collectValidates(
                this.validateFirstname(),
                this.validateLastname(),
                this.validateSurname()
        );
    }

    public void validateStrict() throws AuthorValidationException {
        collectValidates(
                this.validateId(),
                this.validateFirstname(),
                this.validateLastname(),
                this.validateSurname()
        );
    }

    @SafeVarargs
    private void collectValidates(Map<String, String>... maps) throws AuthorValidationException {
        var errors = MapUtils.merge(maps);
        if (errors == null || errors.isEmpty()) {
            return;
        }
        throw new AuthorValidationException(errors);
    }

    private Map<String, String> validateId() {
        return Validator.validateLong(ID_KEY, this.id, 1L, null);
    }

    private Map<String, String> validateFirstname() {
        return Validator.validateString(FIRSTNAME_KEY, this.firstname, 2, 255);
    }

    private Map<String, String> validateLastname() {
        return Validator.validateString(LASTNAME_KEY, this.lastname, 2, 255);
    }

    private Map<String, String> validateSurname() {
        return Validator.validateString(SURNAME_KEY, this.surname, 2, 255);
    }
}