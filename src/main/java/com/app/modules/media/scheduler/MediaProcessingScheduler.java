package com.app.modules.media.scheduler;

import com.app.core.config.AsyncConfig;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.service.MediaProcessingService;
import com.app.modules.media.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class MediaProcessingScheduler {
    private final TaskService taskService;
    private final MediaProcessingService processingService;

    private static final int PENDING_TASKS_LIMIT = 5;

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    protected void convertMediaFiles() {
        loadPendingTasks();
    }

    protected void loadPendingTasks() {
        log.debug("try find pending tasks");

        List<MediaTask> tasks = taskService.findTasks(
                PENDING_TASKS_LIMIT, TaskStatus.PENDING);
        if (tasks.isEmpty()) {
            log.debug("no pending tasks found");
            return;
        }

        log.info("found {} pending tasks", tasks.size());

        tasks.forEach(
                task -> {
                    runProcessTaskAsync(task.getUuid());
                    log.debug("start process task={}", task.getUuid());
                });
    }

    @Async(AsyncConfig.TASK)
    protected void runProcessTaskAsync(@NonNull UUID uuid) {
        processingService.processTask(uuid);
    }
}
