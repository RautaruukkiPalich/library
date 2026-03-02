package com.app.service;

import com.app.dto.GenreDTO;
import com.app.filter.GenreFilter;
import com.app.model.Genre;

import java.util.List;

public interface IGenreService {
    List<Genre> getAll();
    List<Genre> getAll(GenreFilter filter);
    Long add(GenreDTO dto);
    Genre getByID(Long id);
    void delete(Long id);
}