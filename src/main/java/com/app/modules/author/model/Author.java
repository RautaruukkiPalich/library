package com.app.modules.author.model;

import com.app.core.model.BaseModel;
import com.app.core.utils.validator.NumberValidator;
import com.app.core.utils.validator.StringValidator;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.exception.AuthorValidationException;
import com.app.modules.book.model.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "authors")
public class Author extends BaseModel {
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

    public Author() {
        super(AuthorValidationException::new);
    }

    public Author(
            String firstname,
            String lastname,
            String surname
    ) {
        super(AuthorValidationException::new);
        this.firstname = firstname;
        this.lastname = lastname;
        this.surname = surname;
    }

    public Author(AuthorDTO dto) {
        super(AuthorValidationException::new);
        Objects.requireNonNull(dto, "dto must not be null");

        this.firstname = dto.firstname();
        this.lastname = dto.lastname();
        this.surname = dto.surname();
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
    }

    private void addStrictValidations(List<Map<String, String>> list) {
        list.add(this.validateId());
    }

    private static final String ID_KEY = "id";
    private static final String FIRSTNAME_KEY = "firstname";
    private static final String LASTNAME_KEY = "lastname";
    private static final String SURNAME_KEY = "surname";

    private Map<String, String> validateId() {
        return new NumberValidator<>(ID_KEY, this.id)
                .notNull()
                .min(1L)
                .validate();
    }

    private Map<String, String> validateFirstname() {
        return new StringValidator(FIRSTNAME_KEY, this.firstname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
    }

    private Map<String, String> validateLastname() {
        return new StringValidator(LASTNAME_KEY, this.lastname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
    }

    private Map<String, String> validateSurname() {
        return new StringValidator(SURNAME_KEY, this.surname)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
    }
}
