package com.app.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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


    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;


    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}