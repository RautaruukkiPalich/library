package com.app.service;

import com.app.dto.GenreDTO;
import com.app.filter.GenreFilter;
import com.app.model.Genre;
import com.app.repository.IGenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Primary
@Service
@Transactional
public class GenreService implements IGenreService{
    private final IGenreRepository genreRepo;

    public GenreService(IGenreRepository genreRepo){
        this.genreRepo = genreRepo;
    }

    @Override
    public List<Genre> getAll() {
        return this.genreRepo.getAll();
    }

    @Override
    public List<Genre> getAll(GenreFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");

        return this.genreRepo.getAll(filter);
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
    public Genre getByID(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return this.genreRepo.getByID(id);
    }

    @Override
    public void delete(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        Genre genre = this.getByID(id);
        this.genreRepo.delete(genre);
    }
}
