package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaSize;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record MediaFileDTO(
        UUID uuid,
        String filename,
        String extension,
        String contentType,
        UUID mediaUuid,
        MediaSize mediaSize,
        Long fileSize,
        String path,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public String generateFilename() {
        return "%s_%s.%s".formatted(this.mediaUuid(), this.mediaSize(), this.extension());
    }
}
