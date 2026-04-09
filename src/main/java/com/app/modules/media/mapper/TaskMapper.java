package com.app.modules.media.mapper;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.model.MediaTask;

public class TaskMapper {
    public static TaskStatusDTO convert(MediaTask task) {
        return TaskStatusDTO.builder()
                .taskUUID(task.getUuid())
                .mediaUUID(task.getMediaUuid())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();
    }
}
