package com.app.modules.media.service;

import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.TaskTransitionException;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskDeleterRepository;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final TaskDeleterRepository taskDeleterRepository;

    private final MediaSizeService mediaSizeService;

    public MediaTask create(@NonNull Media media,
                            @NonNull ConversionParams conversionParams) {
        MediaTask task = MediaTask.create(
                media.getUserId(), media.getUuid(), conversionParams);
        var savedTask = taskPersistRepository.save(task);
        log.info("created convert task={} for media={}; extension={}",
                savedTask.getUuid(), media.getUuid(), conversionParams.getTargetExtension());
        return savedTask;
    }

    public void deleteByMediaUuid(@NonNull UUID mediaUuid) {
        taskGetterRepository.findByMediaUuid(mediaUuid).forEach(taskDeleterRepository::delete);
    }

    public void setCompleteStatus(@NonNull MediaTask task
    ) throws TaskTransitionException {
        setStatus(task, TaskStatus.COMPLETED);
    }

    public void setFailedStatus(@NonNull MediaTask task,
                                @NonNull String desc
    ) throws TaskTransitionException {
        setStatus(task, TaskStatus.FAILED);
        task.setFailReason(desc);
    }

    public void setPendingStatus(@NonNull MediaTask task
    ) throws TaskTransitionException {
        setStatus(task, TaskStatus.PENDING);
    }

    public void setStatus(@NonNull MediaTask task,
                          @NonNull TaskStatus target
    ) throws TaskTransitionException {
        TaskStatus current = task.getStatus();

        if (current.canTransitionTo(target)) {
            task.setStatus(target);
            return;
        }

        log.error("can not cast task {} status {} to {}", task.getUuid(), current, target);
        throw TaskTransitionException.invalidStatus(current, target);
    }

    public void createBaseConverts(@NonNull Media media) {
        MediaContent content = media.getMediaContent();
        Set<MediaSize> defaultConvertSizes = mediaSizeService.getDefaultConvertSizes(content);
        if (defaultConvertSizes.isEmpty()) {
            log.warn("no default sizes configured for {}", content);
            return;
        }

        log.info("creating conversion tasks {}:{} for media {}",
                content, defaultConvertSizes.size(), media.getUuid());

        defaultConvertSizes.forEach(size -> {
            var config = mediaSizeService.getSizeConfig(content, size);
            if (config == null) {
                log.error("size config not found for {}:{}", content, size);
                return;
            }

            ConversionParams cp = ConversionParams.fromConfig(
                    content, size, content.getProps().targetExtension(), config);

            MediaTask t = MediaTask.create(media.getUserId(), media.getUuid(), cp);
            taskPersistRepository.save(t);
        });
    }
}
