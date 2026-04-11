package com.app.modules.media.scheduler;

import com.app.modules.media.api.MediaProcessingService;
import com.app.modules.media.enums.UploadStatus;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskGetterRepository;
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
    private final TaskGetterRepository taskGetterRepository;
    private final MediaProcessingService processingService;

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    protected void convertMediaFiles() {
        loadPendingTasks();
    }

    private void loadPendingTasks() {
        List<MediaTask> tasks = taskGetterRepository.findByStatus(UploadStatus.PENDING, 5);

        for (MediaTask task : tasks) {
            runProcessTaskAsync(task.getUuid());
            log.debug("start process task={}", task.getUuid());
        }
    }

    @Async
    public void runProcessTaskAsync(@NonNull UUID uuid) {
        processingService.processTask(uuid);
    }
}
