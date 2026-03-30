package com.app.modules.genre.impl;

import com.app.modules.genre.api.GenreService;
import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.mapper.GenreMapper;
import com.app.modules.genre.model.Genre;
import com.app.modules.genre.repository.GenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Primary
@Service
@Transactional
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepo;

    public GenreServiceImpl(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public List<GenreDTO> getAll() {
        return GenreMapper.toListDTO(this.genreRepo.getAll());
    }

    @Override
    public List<GenreDTO> getAll(GenreFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");

        return GenreMapper.toListDTO(this.genreRepo.getAll(filter));
    }

    @Override
    public Long add(GenreDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        Genre genre = new Genre(dto);
        genre.validate();

        Genre savedGenre = this.genreRepo.save(genre);
        savedGenre.validateStrict();

        return savedGenre.getId();
    }

    @Override
    public GenreDTO getByID(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return GenreMapper.toDTO(this.getByIDInternal(id));
    }

    @Override
    public void delete(Long id) {
        Genre genre = this.getByIDInternal(id);
        this.genreRepo.delete(genre);
    }

    private Genre getByIDInternal(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return this.genreRepo.getByID(id);
    }
}
