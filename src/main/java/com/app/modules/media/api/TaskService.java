package com.app.modules.media.api;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.model.Media;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskStatusDTO create(Long userId, UUID mediaUuid);

    TaskStatusDTO status(@NonNull UUID uuid);

    List<TaskStatusDTO> getUserTasks(@NonNull Long userId);

    void update(@NonNull UUID taskUUID,
                @NonNull TaskStatus newStatus);

    void delete(@NonNull UUID taskUUID);

    void deleteByMediaUuid(@NonNull UUID mediaUuid);

    TaskStatusDTO createImageConvertTask(@NonNull Media media,
                                         @NonNull MediaSize size);

    TaskStatusDTO createConvertTask(@NonNull Media media,
                                    @NonNull MediaMetadata metadata);

    void prepareTaskStatus(@NonNull UUID taskUuid,
                           @NonNull TaskStatus status);
}
