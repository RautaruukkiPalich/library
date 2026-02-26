package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
    
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "pub_year")
    private Integer pubYear;
    
    @Column(length = 20)
    private String isbn;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
    
    @Column(name = "page_count")
    private Integer pageCount;

    public Book() {}

    public Book(
        String title,
        Author author,
        Genre genre,
        String isbn,
        int pubYear,
        int pageCount,
        boolean isAvailable
    ){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.pubYear = pubYear;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
        this.pageCount = pageCount;
    };

    public Long getId() { return this.id; }
    public String getTitle() { return this.title; }
    public Author getAuthor() { return this.author; }
    public Genre getGenre() { return this.genre; }
    public int getPubYear() { return this.pubYear; }
    public String getIsbn() { return this.isbn; }
    public boolean isAvailable() { return this.isAvailable; }
    public int getPageCount() { return this.pageCount; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(Author author) { this.author = author; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setPubYear(int pubYear) { this.pubYear = pubYear; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public void setAvailable() { this.isAvailable = true; }
    public void setBorrow() { this.isAvailable = false; }
}
