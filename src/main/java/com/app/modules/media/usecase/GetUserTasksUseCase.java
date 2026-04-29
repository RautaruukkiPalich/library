package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskGetterRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class GetUserTasksUseCase extends BaseQueryUseCase<GetUserTasksUseCase.Input, List<TaskStatusDTO>> {
    private final TaskGetterRepository taskGetterRepository;


    @Override
    public List<TaskStatusDTO> execute(@Valid @NonNull Input input) {
        Pageable pageable = PageRequest.of(input.page(), input.pageSize());

        List<MediaTask> tasks = taskGetterRepository.find(
                input.userId(),
                pageable,
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
