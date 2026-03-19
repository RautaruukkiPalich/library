package com.app.modules.refresh_token.dto;

import lombok.Builder;

@Builder
public record RefreshTokenInfoDTO(
        String token,
        Long userId
) {
}
