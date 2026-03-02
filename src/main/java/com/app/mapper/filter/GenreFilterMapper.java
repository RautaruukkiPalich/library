package com.app.mapper.filter;

import com.app.dto.queryparams.GenreQueryParamsDTO;
import com.app.filter.GenreFilter;

import java.util.Objects;

public class GenreFilterMapper {
    public static GenreFilter toFilter(GenreQueryParamsDTO params) {
        Objects.requireNonNull(params, "params cant be null");
        return GenreFilter.builder().build();
    }
}
