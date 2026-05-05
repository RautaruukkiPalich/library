package com.app.modules.media.service;

import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.exceptions.TaskTransitionException;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MediaSizeService mediaSizeService;

    public MediaTask createTask(@NonNull Media media,
                                @NonNull ConversionParams conversionParams) {
        MediaTask task = MediaTask.create(
                media.getUserId(), media.getUuid(), conversionParams);
        var savedTask = taskRepository.save(task);
        log.info("created convert task={} for media={}; extension={}",
                savedTask.getUuid(), media.getUuid(), conversionParams.getTargetExtension());
        return savedTask;
    }

    public MediaTask getTask(@NonNull UUID uuid) {
        return taskRepository.findById(uuid).orElseThrow(() -> MediaTaskNotFoundException.uuid(uuid));
    }

    public Long countTasks(@NonNull Long userId,
                           TaskStatus status) {
        return taskRepository.count(userId, status);
    }

    public List<MediaTask> findUserTasks(@NonNull Long userId,
                                         @NonNull Integer page,
                                         @NonNull Integer pageSize,
                                         @NonNull SortOrder sortOrder,
                                         TaskStatus status) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return taskRepository.find(userId, pageable, sortOrder, status);
    }

    public List<MediaTask> findTasks(@NonNull Integer limit,
                                     TaskStatus status) {
        Pageable pageable = PageRequest.of(0, limit);
        return taskRepository.find(null, pageable, null, status);
    }

    public void deleteByMediaUuid(@NonNull UUID mediaUuid) {
        taskRepository.findAllByMediaUuid(mediaUuid).forEach(taskRepository::delete);
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
            save(task);
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

            createTask(media, cp);
        });
    }

    public MediaTask save(@NonNull MediaTask task) {
        return taskRepository.save(task);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MediaTask saveNested(@NonNull MediaTask task) {
        return save(task);
    }
}
