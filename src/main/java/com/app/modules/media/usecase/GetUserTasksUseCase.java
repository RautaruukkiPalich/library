package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class GetUserTasksUseCase extends BaseQueryUseCase<GetUserTasksUseCase.Input, List<TaskStatusDTO>> {
    private final TaskService taskService;

    @Override
    public List<TaskStatusDTO> execute(@Valid @NonNull Input input) {
        List<MediaTask> tasks = taskService.findUserTasks(
                input.userId(),
                input.page(),
                input.pageSize(),
                input.order(),
                input.status());
        return tasks.stream().map(TaskMapper::convert).toList();
    }

    public record Input(
            @NotNull Long userId,
            @NotNull @Min(0) Integer page,
            @NotNull @Min(1) @Max(20) Integer pageSize,
            TaskStatus status,
            SortOrder order
    ) {
        public Input {
            if (order == null) {
                order = SortOrder.ASC;
            }
        }
    }
}
