package com.app.modules.genre.mapper;

import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.dto.GenreQueryParamsDTO;

import java.util.Objects;

public class GenreFilterMapper {
    public static GenreFilter toFilter(GenreQueryParamsDTO params) {
        Objects.requireNonNull(params, "params must not be null");
        return GenreFilter.builder().build();
    }
}
