package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskGetterRepository;
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
public class GetTaskUseCase extends BaseQueryUseCase<GetTaskUseCase.Input, TaskStatusDTO> {
    private final TaskGetterRepository taskGetterRepository;
    private final CheckPermissionService checkPermissionService;


    @Override
    public TaskStatusDTO execute(@NonNull Input input) {
        MediaTask task = taskGetterRepository.getByUUID(input.taskUuid());
        checkPermissionService.checkTaskPermission(task, input.userId());
        return TaskMapper.convert(task);
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID taskUuid
    ) {
    }
}
