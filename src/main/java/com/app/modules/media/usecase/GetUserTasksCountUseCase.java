package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class GetUserTasksCountUseCase extends BaseQueryUseCase<GetUserTasksCountUseCase.Input, Long> {

    private final TaskService taskService;

    @Override
    public Long execute(@NonNull Input input) {
        return taskService.countTasks(
                input.userId(),
                input.status());
    }

    public record Input(
            @NonNull Long userId,
            TaskStatus status
    ) {
    }
}
