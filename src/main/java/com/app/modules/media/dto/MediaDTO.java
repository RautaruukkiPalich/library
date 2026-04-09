package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
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
        MediaContentType mediaType,
        MediaPurpose purpose,
        List<MediaFileDTO> files,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
