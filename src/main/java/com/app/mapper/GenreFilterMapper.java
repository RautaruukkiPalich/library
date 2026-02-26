package com.app.mapper;

import com.app.dto.GenreQueryParamsDTO;
import com.app.filter.GenreFilter;

public class GenreFilterMapper {
    public static GenreFilter toFilter(GenreQueryParamsDTO params){
        return new GenreFilter();
    }
}
