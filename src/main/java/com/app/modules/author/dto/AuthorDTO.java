package com.app.modules.author.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record AuthorDTO(
        Long id,
        String firstname,
        String lastname,
        String surname,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
