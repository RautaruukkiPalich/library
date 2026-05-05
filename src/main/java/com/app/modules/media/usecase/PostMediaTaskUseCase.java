package com.app.modules.media.usecase;

import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.service.CheckPermissionService;
import com.app.modules.media.service.MediaService;
import com.app.modules.media.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class PostMediaTaskUseCase extends BaseCommandUseCase<PostMediaTaskUseCase.Input, TaskStatusDTO> {
    private final MediaService mediaService;
    private final TaskService taskService;
    private final CheckPermissionService checkPermissionService;

    @Override
    public TaskStatusDTO execute(@NonNull Input input) {
        Media media = mediaService.getMedia(input.mediaUuid());
        checkPermissionService.checkCanEdit(media, input.userId());

        MediaTask savedTask = taskService.createTask(media, input.params);

        return TaskMapper.convert(savedTask);
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid,
            @NotNull ConversionParams params
    ) {
    }
}
