package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaContent;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MediaDTO(
        UUID uuid,
        Long userId,
        String originalFilename,
        Boolean isPublic,
        MediaContent mediaContent,
        List<MediaFileDTO> files,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
