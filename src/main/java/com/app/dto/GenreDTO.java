package com.app.dto;

import lombok.Builder;

@Builder
public record GenreDTO(
        Long id,
        String name
) {
}
