package com.app.model;

import java.util.List;
import java.util.Objects;

import com.app.dto.AuthorDTO;
import com.app.exception.validation.AuthorValidationException;
import jakarta.persistence.CascadeType;
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
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String firstname;
    
    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(nullable = false, length = 100)
    private String surname;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;

    public Author(){}

    public Author(
        String firstname,
        String lastname,
        String surname
    ){
        this.firstname = firstname;
        this.lastname = lastname;
        this.surname = surname;
    }

    public Author(AuthorDTO dto){
        Objects.requireNonNull(dto, "dto must not be null");

        this.firstname = dto.firstname();
        this.lastname = dto.lastname();
        this.surname = dto.surname();
    }

    public void validate() throws AuthorValidationException {
        this.validateFirstname();
        this.validateLastname();
        this.validateSurname();
    }

    public void validateStrict() throws AuthorValidationException {
        if (this.getId() == null){
            throw new AuthorValidationException("id", "cant be less than 1");
        }
        this.validate();
    }

    private void validateFirstname() throws AuthorValidationException{
        if (this.firstname == null){
            throw new AuthorValidationException("firstname", "cant be null");
        }
        if (this.firstname.isBlank()){
            throw new AuthorValidationException("firstname", "cant be blank");
        }
        if (this.firstname.length() < 2){
            throw new AuthorValidationException("firstname", "cant be shorter 2 characters");
        }
        if (this.firstname.length() > 255){
            throw new AuthorValidationException("firstname", "cant be longer 255 characters");
        }
    }

    private void validateLastname() throws AuthorValidationException{
        if (this.lastname == null){
            throw new AuthorValidationException("lastname", "cant be null");
        }
        if (this.lastname.isBlank()){
            throw new AuthorValidationException("lastname", "cant be blank");
        }
        if (this.lastname.length() < 2){
            throw new AuthorValidationException("lastname", "cant be shorter 2 characters");
        }
        if (this.lastname.length() > 255){
            throw new AuthorValidationException("lastname", "cant be longer 255 characters");
        }
    }

    private void validateSurname() throws AuthorValidationException{
        if (this.surname == null){
            throw new AuthorValidationException("surname", "cant be null");
        }
        if (this.surname.isBlank()){
            throw new AuthorValidationException("surname", "cant be blank");
        }
        if (this.surname.length() < 2){
            throw new AuthorValidationException("surname", "cant be shorter 2 characters");
        }
        if (this.surname.length() > 255){
            throw new AuthorValidationException("surname", "cant be longer 255 characters");
        }
    }
}