package com.app.service;

import java.util.List;

import com.app.repository.IBookRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.app.dto.BookDTO;
import com.app.exception.BookValidationException;
import com.app.filter.BookFilter;
import com.app.model.Author;
import com.app.model.Book;
import com.app.model.Genre;
import com.app.repository.IAuthorRepository;
import com.app.repository.IGenreRepository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@Transactional
public class BookService implements IBookService {

    private final IBookRepository bookRepo;
    private final IAuthorRepository authorRepo;
    private final IGenreRepository genreRepo;

    public BookService(
        IBookRepository bookRepo,
        IAuthorRepository authorRepo,
        IGenreRepository genreRepo
    ){
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> GetAll() {
        return this.bookRepo.getAll();
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<Book> GetAll(BookFilter filter) {
        return this.bookRepo.getAll(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Book GetByID(Long id) {
        return this.bookRepo.getByID(id);
    }

    @Override
    public void Add(BookDTO dto) {
        if (dto.pubYear == null ){
            throw new BookValidationException("pubYear", "field must be NOT NULL");
        }
        if (dto.isAvailable == null){
            throw new BookValidationException("isAvailable", "field must be NOT NULL");
        }
        if (dto.pageCount == null){
            throw new BookValidationException("pageCount", "field must be NOT NULL");
        }

        Author author = this.authorRepo.getByID(dto.author_id);
        Genre genre = this.genreRepo.getByID(dto.genre_id);

        Book book = new Book();

        book.setTitle( dto.title);
        book.setAuthor(author); 
        book.setGenre(genre);
        book.setPubYear(dto.pubYear); 
        book.setIsbn( dto.isbn);
        book.setAvailable(dto.isAvailable);
        book.setPageCount(dto.pageCount); 

        this.bookRepo.save(book);
    }

    @Override
    public void Delete(Long id) {
        this.bookRepo.deleteByID(id);
    }

    @Override
    public void PutByID(Long id, BookDTO dto) {
        if (dto.pubYear == null ){
            throw new BookValidationException("pubYear", "field must be NOT NULL");
        }
        if (dto.isAvailable == null){
            throw new BookValidationException("isAvailable", "field must be NOT NULL");
        }
        if (dto.pageCount == null){
            throw new BookValidationException("pageCount", "field must be NOT NULL");
        }

        Author author = this.authorRepo.getByID(dto.author_id);
        Genre genre = this.genreRepo.getByID(dto.genre_id);
        Book book = this.GetByID(dto.id);

        book.setTitle( dto.title);
        book.setAuthor(author); 
        book.setGenre(genre);
        book.setPubYear(dto.pubYear); 
        book.setIsbn( dto.isbn);
        book.setAvailable(dto.isAvailable);
        book.setPageCount(dto.pageCount); 

        this.bookRepo.save(book);
    }

    @Override
    public void PatchByID(Long id, BookDTO dto) {
        Book book = this.GetByID(id);
        if (dto.title != null) {
            book.setTitle(dto.title);
        }
        if (dto.author_id != null) {
            book.setAuthor(this.authorRepo.getByID(dto.author_id));
        }
        if (dto.genre_id != null) {
            book.setGenre(this.genreRepo.getByID(dto.genre_id));
        }
        if (dto.pubYear != null) {
            book.setPubYear(dto.pubYear);
        }
        if (dto.isbn != null) {
            book.setIsbn(dto.isbn);
        }
        if (dto.isAvailable != null) {
            book.setAvailable(dto.isAvailable);
        }
        if (dto.pageCount != null) {
            book.setPageCount(dto.pageCount);
        }

        this.bookRepo.save(book);

    }
}
