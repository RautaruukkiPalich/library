package com.app.mapper.filter;

import com.app.dto.queryparams.GenreQueryParamsDTO;
import com.app.filter.GenreFilter;

public class GenreFilterMapper {
    public static GenreFilter toFilter(GenreQueryParamsDTO params){
        return new GenreFilter();
    }
}
