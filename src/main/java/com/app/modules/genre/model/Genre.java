package com.app.modules.genre.model;

import com.app.core.model.BaseModel;
import com.app.core.utils.validator.NumberValidator;
import com.app.core.utils.validator.StringValidator;
import com.app.modules.book.model.Book;
import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.exception.GenreValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "genres")
public class Genre extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<Book> books;

    public Genre(@NonNull GenreDTO dto) {
        super(GenreValidationException::new);
        this.name = dto.name();
    }

    public Genre() {
        super(GenreValidationException::new);
    }

    public Genre(@NonNull String name) {
        super(GenreValidationException::new);
        this.name = name;
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
        list.add(this.validateName());
    }

    private void addStrictValidations(List<Map<String, String>> list) {
        list.add(this.validateId());
    }

    private static final String ID_KEY = "id";
    private final static String NAME_KEY = "name";

    private Map<String, String> validateId() {
        return new NumberValidator<>(ID_KEY, this.id)
                .notNull()
                .min(1L)
                .validate();
    }

    private Map<String, String> validateName() {
        return new StringValidator(NAME_KEY, this.name)
                .notNull()
                .notBlank()
                .minLength(2)
                .maxLength(255)
                .validate();
    }
}