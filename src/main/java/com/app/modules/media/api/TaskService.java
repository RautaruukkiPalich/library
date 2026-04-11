package com.app.modules.media.api;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.UploadStatus;
import lombok.NonNull;

import java.util.UUID;

public interface TaskService {
    TaskStatusDTO create(Long userId, UUID mediaUuid);

    TaskStatusDTO status(@NonNull UUID uuid);

    void update(@NonNull UUID taskUUID,
                @NonNull UploadStatus newStatus);

    void delete(@NonNull UUID taskUUID);

    void deleteByMediaUuid(@NonNull UUID mediaUuid);
}
