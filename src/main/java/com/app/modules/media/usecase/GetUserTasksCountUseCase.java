package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.repository.TaskGetterRepository;
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

    private final TaskGetterRepository taskGetterRepository;

    @Override
    public Long execute(@org.jspecify.annotations.NonNull Input input) {
        return taskGetterRepository.count(
                input.userId(),
                input.status());
    }

    public record Input(
            @NonNull Long userId,
            TaskStatus status
    ) {
    }
}
