package com.app.service;

import com.app.dto.AuthorDTO;
import com.app.filter.AuthorFilter;
import com.app.model.Author;
import com.app.repository.IAuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@Transactional
public class AuthorService implements IAuthorService{

    private final IAuthorRepository authorRepo;

    public AuthorService(
            IAuthorRepository authorRepo
    ){
        this.authorRepo = authorRepo;
    }

    @Override
    public List<Author> getAll() {
        return this.authorRepo.getAll();
    }

    @Override
    public List<Author> getAll(AuthorFilter filter) {
        return this.authorRepo.getAll(filter);
    }

    @Override
    public void add(AuthorDTO dto) {
        Author author = new Author();
        author.setFirstname(dto.firstname);
        author.setLastname(dto.lastname);
        author.setSurname(dto.surname);

        this.authorRepo.save(author);
    }

    @Override
    public Author getByID(Long id){
        return this.authorRepo.getByID(id);
    }

    @Override
    public void delete(Long id) {
        this.authorRepo.deleteByID(id);
    }
}
