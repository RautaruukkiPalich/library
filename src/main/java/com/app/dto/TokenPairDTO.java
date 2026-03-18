package com.app.dto;

import lombok.Builder;

@Builder
public record TokenPairDTO(
        String access,
        String refresh
) {
}
