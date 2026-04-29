package com.app.modules.media.dto;

import com.app.modules.media.enums.TaskStatus;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TaskStatusDTO(
        UUID taskUUID,
        UUID mediaUUID,
        Long userId,
        TaskStatus status,
        String failReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
