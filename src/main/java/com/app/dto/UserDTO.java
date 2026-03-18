package com.app.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String surname,
        String email,
        String rawPassword,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
