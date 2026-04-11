package com.app.modules.media.dto;

import com.app.modules.media.enums.UploadStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TaskStatusDTO(
        UUID taskUUID,
        UUID mediaUUID,
        Long userId,
        UploadStatus status
) {
}
