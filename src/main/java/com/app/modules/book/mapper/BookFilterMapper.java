package com.app.modules.book.mapper;

import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.dto.BookQueryParamsDTO;
import lombok.NonNull;

public class BookFilterMapper {
    public static BookFilter toFilter(@NonNull BookQueryParamsDTO.TitleGenre dto) {
        return BookFilter.builder().title(dto.getTitle()).genre(dto.getGenre()).build();
    }

    public static BookFilter available() {
        return BookFilter.builder().isAvailable(true).build();
    }

    public static BookFilter year(@NonNull Integer year) {
        return BookFilter.builder().pubYearTo(year).pubYearFrom(year).build();
    }

    public static BookFilter betweenYears(@NonNull Integer from, @NonNull Integer to) {
        return BookFilter.builder().pubYearFrom(from).pubYearTo(to).build();
    }
}
