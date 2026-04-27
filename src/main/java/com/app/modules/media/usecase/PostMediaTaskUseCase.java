package com.app.modules.media.usecase;

import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.converter.media.ConversionParams;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import com.app.modules.media.service.CheckPermissionService;
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
    private final MediaGetterRepository mediaGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final CheckPermissionService checkPermissionService;

    @Override
    public TaskStatusDTO execute(@NonNull Input input) {
        Media media = mediaGetterRepository.getByUuid(input.mediaUuid());
        checkPermissionService.checkCanEdit(media, input.userId());

        MediaTask task = MediaTask.create(input.userId(), media.getUuid(), input.params());
        MediaTask savedTask = taskPersistRepository.save(task);

        return TaskMapper.convert(savedTask);
    }

    //TODO: hardcode
    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid,
            @NotNull ConversionParams params
    ) {
    }
}
