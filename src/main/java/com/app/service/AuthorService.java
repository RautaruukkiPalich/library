package com.app.service;

import com.app.dto.AuthorDTO;
import com.app.exception.validation.AuthorValidationException;
import com.app.filter.AuthorFilter;
import com.app.mapper.AuthorMapper;
import com.app.model.Author;
import com.app.repository.IAuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Primary
@Service
@Transactional
public class AuthorService implements IAuthorService {

    private final IAuthorRepository authorRepo;

    public AuthorService(
            IAuthorRepository authorRepo
    ) {
        this.authorRepo = authorRepo;
    }

    @Override
    public List<AuthorDTO> getAll() {
        return AuthorMapper.toListDTO(this.authorRepo.getAll());
    }

    @Override
    public List<AuthorDTO> getAll(AuthorFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");

        return AuthorMapper.toListDTO(this.authorRepo.getAll(filter));
    }

    @Override
    public Long add(AuthorDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        Author author = new Author(dto);
        author.validate();

        Author savedAuthor = this.authorRepo.save(author);
        savedAuthor.validateStrict();
        return savedAuthor.getId();
    }

    @Override
    public AuthorDTO getByID(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        this.validateId(id);
        return AuthorMapper.toDTO(this.authorRepo.getByID(id));
    }

    @Override
    public void delete(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        this.validateId(id);
        Author author = this.authorRepo.getByID(id);
        this.authorRepo.delete(author);
    }

    private void validateId(Long id) throws AuthorValidationException {
        if (id < 1) {
            throw new AuthorValidationException("id", "cant be less than 1");
        }
    }
}
