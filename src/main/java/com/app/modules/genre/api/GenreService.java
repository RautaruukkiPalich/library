package com.app.modules.genre.api;

import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.dto.GenreFilter;

import java.util.List;

public interface GenreService {
    List<GenreDTO> getAll();

    List<GenreDTO> getAll(GenreFilter filter);

    Long add(GenreDTO dto);

    GenreDTO getByID(Long id);

    void delete(Long id);
}