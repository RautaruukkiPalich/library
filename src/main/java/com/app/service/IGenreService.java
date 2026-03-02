package com.app.service;

import com.app.dto.GenreDTO;
import com.app.filter.GenreFilter;

import java.util.List;

public interface IGenreService {
    List<GenreDTO> getAll();

    List<GenreDTO> getAll(GenreFilter filter);

    Long add(GenreDTO dto);

    GenreDTO getByID(Long id);

    void delete(Long id);
}