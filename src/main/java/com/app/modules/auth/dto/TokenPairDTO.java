package com.app.modules.auth.dto;

import lombok.Builder;

@Builder
public record TokenPairDTO(
        String access,
        String refresh
) {
}
