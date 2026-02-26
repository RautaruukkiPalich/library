package com.app.mapper;

import com.app.dto.AuthorQueryParamsDTO;
import com.app.filter.AuthorFilter;

public class AuthorFilterMapper {

    public static AuthorFilter toFilter(AuthorQueryParamsDTO params){
        return new AuthorFilter();
    }
}
