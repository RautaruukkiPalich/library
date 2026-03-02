package com.app.mapper.filter;

import com.app.dto.queryparams.BookQueryParamsDTO;
import com.app.filter.BookFilter;

import java.util.Objects;

public class BookFilterMapper {
    public static BookFilter toFilter(BookQueryParamsDTO.TitleGenre dto) {
        Objects.requireNonNull(dto, "dto cant be null");

        return BookFilter.builder().title(dto.getTitle()).genre(dto.getGenre()).build();
    }

    public static BookFilter available() {
        return BookFilter.builder().isAvailable(true).build();
    }

    public static BookFilter year(Integer year) {
        Objects.requireNonNull(year, "year cant be null");

        return BookFilter.builder().pubYearTo(year).pubYearFrom(year).build();
    }

    public static BookFilter betweenYears(Integer from, Integer to) {
        Objects.requireNonNull(from, "from cant be null");
        Objects.requireNonNull(to, "to cant be null");

        return BookFilter.builder().pubYearFrom(from).pubYearTo(to).build();
    }
}
