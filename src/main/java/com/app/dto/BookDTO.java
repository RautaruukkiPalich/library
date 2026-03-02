package com.app.dto;

import lombok.Builder;

@Builder
public record BookDTO(
        Long id,
        String title,
        Long authorId,
        Long genreId,
        Integer pubYear,
        String isbn,
        Boolean isAvailable,
        Integer pageCount
) {
}

