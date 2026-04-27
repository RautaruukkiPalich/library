package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.metadata.MediaMetadata;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record MediaFileDTO(
        UUID uuid,
        String filename,
        UUID mediaUuid,
        MediaSize mediaSize,
        String contentType,
        MediaMetadata metadata,
        String path,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public String generateFilename() {
        return "%s_%s_%s.%s".formatted(
                this.mediaUuid, this.mediaSize, this.metadata.getSpecDesc(), this.metadata.getExtension());
    }
}
