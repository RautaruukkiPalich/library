package com.app.modules.media.service;

import com.app.modules.media.converter.ConversionParams;
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

import java.util.Objects;
import java.util.UUID;

import static com.app.modules.media.enums.MediaContent.IMAGE;
import static com.app.modules.media.enums.MediaSize.DEFAULT_MEDIA_SIZE;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final TaskDeleterRepository taskDeleterRepository;

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
        if (Objects.requireNonNull(media.getMediaContent()) == IMAGE) {
            createBaseImageConverts(media);
        }
    }

    private void createBaseImageConverts(@NonNull Media media) {
        for (MediaSize size : DEFAULT_MEDIA_SIZE) {
            ConversionParams cp = ConversionParams.builder()
                    .targetType(IMAGE)
                    .targetExtension(IMAGE.getProps().targetExtension())
                    .targetSize(size)
                    .width(size.getWidth())
                    .height(size.getHeight())
                    .keepAspectRatio(size.isKeepAspectRatio())
                    .cropToSquare(size.isCropToSquare())
                    .build();
            MediaTask t = MediaTask.create(media.getUserId(), media.getUuid(), cp);
            taskPersistRepository.save(t);
        }
    }
}
