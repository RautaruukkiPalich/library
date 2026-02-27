package com.app.mapper.filter;

import com.app.dto.queryparams.AuthorQueryParamsDTO;
import com.app.filter.AuthorFilter;

public class AuthorFilterMapper {

    public static AuthorFilter toFilter(AuthorQueryParamsDTO params){
        return new AuthorFilter();
    }
}
