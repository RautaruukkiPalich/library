package com.app.modules.media.dto;

import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record DownloadedMediaDTO(
        Resource file,
        Long ownerId,
        String contentType,
        String extension,
        Long fileSize
) {
}
