package com.app.mapper.filter;

import com.app.dto.queryparams.AuthorQueryParamsDTO;
import com.app.filter.AuthorFilter;

import java.util.Objects;

public class AuthorFilterMapper {

    public static AuthorFilter toFilter(AuthorQueryParamsDTO params) {
        Objects.requireNonNull(params, "params cant be null");

        return AuthorFilter.builder().build();
    }
}
