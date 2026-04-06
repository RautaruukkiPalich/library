package com.app.modules.genre.mapper;

import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.dto.GenreQueryParamsDTO;
import lombok.NonNull;


public class GenreFilterMapper {
    public static GenreFilter toFilter(@NonNull GenreQueryParamsDTO params) {
        return GenreFilter.builder().build();
    }
}
