package com.app.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record GenreDTO(
        Long id,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
