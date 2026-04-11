package com.app.modules.media.dto;

import lombok.Builder;

@Builder
public record ConvertResultDTO(
        String path,
        String extension,
        Long size
) {
}
