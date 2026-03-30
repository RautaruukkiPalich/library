package com.app.modules.book.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record BookDTO(
        Long id,
        String title,
        Long authorId,
        Long genreId,
        Integer pubYear,
        String isbn,
        Boolean isAvailable,
        Integer pageCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

