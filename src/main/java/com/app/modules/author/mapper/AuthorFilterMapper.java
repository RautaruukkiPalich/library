package com.app.modules.author.mapper;

import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.dto.AuthorQueryParamsDTO;

import java.util.Objects;

public class AuthorFilterMapper {

    public static AuthorFilter toFilter(AuthorQueryParamsDTO params) {
        Objects.requireNonNull(params, "params cant be null");

        return AuthorFilter.builder().build();
    }
}
