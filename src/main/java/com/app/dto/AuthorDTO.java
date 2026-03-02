package com.app.dto;

import lombok.Builder;

@Builder
public record AuthorDTO(
        Long id,
        String firstname,
        String lastname,
        String surname
) {
}
