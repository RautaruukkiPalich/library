package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaSize;
import lombok.Builder;

@Builder
public record FileMetadata(
        String filename,
        String extension,
        String contentType,
        Integer width,
        Integer height,
        MediaSize mediaSize,
        Long fileSize,
        String relativePath
) {
}
