package com.app.modules.genre.impl;

import com.app.modules.genre.api.GenreService;
import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.mapper.GenreMapper;
import com.app.modules.genre.model.Genre;
import com.app.modules.genre.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@Transactional
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepo;

    public GenreServiceImpl(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    public List<GenreDTO> getAll() {
        return GenreMapper.toListDTO(this.genreRepo.findAll());
    }

    public List<GenreDTO> getAll(@NonNull GenreFilter filter) {
        return GenreMapper.toListDTO(this.genreRepo.findAll(filter));
    }

    public Long add(@NonNull GenreDTO dto) {
        Genre genre = new Genre(dto);
        genre.validate();

        Genre savedGenre = this.genreRepo.save(genre);
        savedGenre.validateStrict();

        return savedGenre.getId();
    }

    public GenreDTO getByID(@NonNull Long id) {
        return GenreMapper.toDTO(this.getByIDInternal(id));
    }

    public void delete(@NonNull Long id) {
        Genre genre = this.getByIDInternal(id);
        this.genreRepo.delete(genre);
    }

    private Genre getByIDInternal(@NonNull Long id) {
        return this.genreRepo.getById(id);
    }
}
