package com.app.modules.author.impl;

import com.app.modules.author.api.AuthorService;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.exception.AuthorValidationException;
import com.app.modules.author.mapper.AuthorMapper;
import com.app.modules.author.model.Author;
import com.app.modules.author.repository.AuthorRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Primary
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepo;

    public AuthorServiceImpl(
            AuthorRepository authorRepo
    ) {
        this.authorRepo = authorRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDTO> getAll() {
        return AuthorMapper.toListDTO(this.authorRepo.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDTO> getAll(@NonNull AuthorFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");

        return AuthorMapper.toListDTO(this.authorRepo.findAll(filter));
    }

    @Override
    public Long add(@NonNull AuthorDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        Author author = new Author(dto);
        author.validate();

        Author savedAuthor = this.authorRepo.save(author);
        savedAuthor.validateStrict();
        return savedAuthor.getId();
    }

    @Override
    public AuthorDTO getByID(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");

        this.validateId(id);
        return AuthorMapper.toDTO(this.authorRepo.getById(id));
    }

    @Override
    public void delete(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");

        this.validateId(id);
        Author author = this.authorRepo.getById(id);
        this.authorRepo.delete(author);
    }

    private void validateId(Long id) throws AuthorValidationException {
        if (id < 1) {
            throw new AuthorValidationException("id", "must not be less than 1");
        }
    }
}
