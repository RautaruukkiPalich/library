package com.app.model;

import java.util.List;
import java.util.Objects;

import com.app.dto.GenreDTO;
import com.app.exception.validation.GenreValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<Book> books;

    public Genre(GenreDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        this.name = dto.name();
    }

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public void validate() throws GenreValidationException {
        this.validateName();
    }

    public void validateStrict() throws GenreValidationException {
        if (this.getId() <= 0) {
            throw new GenreValidationException("id", "id cant be less than 1");
        }
        this.validate();
    }

    private void validateName() throws GenreValidationException {
        if (this.name == null) {
            throw new GenreValidationException("name", "cant be null");
        }
        if (this.name.isBlank()) {
            throw new GenreValidationException("name", "cant be blank");
        }
        if (this.name.length() < 2) {
            throw new GenreValidationException("name", "cant be shorter 2 characters");
        }
        if (this.name.length() > 255) {
            throw new GenreValidationException("name", "cant be longer 255 characters");
        }
    }
}