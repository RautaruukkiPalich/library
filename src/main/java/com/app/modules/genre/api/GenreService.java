package com.app.modules.genre.api;

import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.dto.GenreFilter;
import lombok.NonNull;

import java.util.List;

public interface GenreService {
    List<GenreDTO> getAll();

    List<GenreDTO> getAll(@NonNull GenreFilter filter);

    Long add(@NonNull GenreDTO dto);

    GenreDTO getByID(@NonNull Long id);

    void delete(@NonNull Long id);
}