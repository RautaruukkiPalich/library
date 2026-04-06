package com.app.modules.author.mapper;

import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.dto.AuthorQueryParamsDTO;

public class AuthorFilterMapper {

    public static AuthorFilter toFilter(AuthorQueryParamsDTO params) {
        return AuthorFilter.builder().build();
    }
}
