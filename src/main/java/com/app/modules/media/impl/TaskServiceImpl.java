package com.app.modules.media.impl;

import com.app.modules.media.api.TaskService;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.mapper.TaskMapper;
import com.app.modules.media.metadata.ImageMetadataImpl;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskDeleterRepository;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final TaskDeleterRepository taskDeleterRepository;

    @Override
    public TaskStatusDTO create(@NonNull Long userId, @NonNull UUID mediaUuid) {
        MediaTask task = new MediaTask();
        task.setUuid(UUID.randomUUID());
        task.setUserId(userId);
        task.setStatus(TaskStatus.PENDING);
        task.setMediaUuid(mediaUuid);
        task.validate();

        MediaTask savedTask = taskPersistRepository.save(task);
        log.debug("task created: id={}, status={}", savedTask.getUuid(), savedTask.getStatus());

        return TaskMapper.convert(savedTask);
    }

    @Override
    public TaskStatusDTO status(@NonNull UUID taskUUID) {
        MediaTask task = taskGetterRepository.getByUUID(taskUUID);
        return TaskMapper.convert(task);
    }

    @Override
    public List<TaskStatusDTO> getUserTasks(@NonNull Long userId) {
        List<MediaTask> tasks = taskGetterRepository.findByUserId(userId);
        return tasks.stream().map(TaskMapper::convert).toList();
    }

    @Override
    public void update(@NonNull UUID taskUUID,
                       @NonNull TaskStatus newStatus) {
        MediaTask task = taskGetterRepository.getByUUID(taskUUID);
        if (task.getStatus().isFinal() || task.getStatus().isSameOrHigher(newStatus)) {
            return;
        }

        task.setStatus(newStatus);
        taskPersistRepository.save(task);
    }

    @Override
    public void delete(@NonNull UUID taskUUID) {
        taskDeleterRepository.delete(taskUUID);
    }

    @Override
    public void deleteByMediaUuid(@NonNull UUID mediaUuid) {
        taskGetterRepository.findByMediaUuid(mediaUuid).forEach(taskDeleterRepository::delete);
    }

    @Override
    public TaskStatusDTO createImageConvertTask(@NonNull Media media, @NonNull MediaSize size) {
        MediaMetadata metadata = ImageMetadataImpl.create(size);
        return createConvertTask(media, metadata);
    }

    @Override
    public TaskStatusDTO createConvertTask(@NonNull Media media, @NonNull MediaMetadata metadata) {
        MediaTask task = MediaTask.create(
                media.getUserId(), media.getUuid(), metadata);
        var savedTask = taskPersistRepository.save(task);
        log.info("created convert task={} for media={}. size {}x{} {} extension={}," +
                        "content-type={}, keepAspect={}, cropToSquare={}",
                savedTask.getUuid(), media.getUuid(), metadata.getWidth(), metadata.getHeight(),
                metadata.getContentType(), metadata.getExtension(), metadata.getContentType(),
                metadata.getKeepAspectRatio(), metadata.getCropToSquare());
        return TaskMapper.convert(savedTask);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void prepareTaskStatus(@NonNull UUID taskUuid,
                                  @NonNull TaskStatus status) {
        MediaTask task = taskGetterRepository.getByUUID(taskUuid);
        task.setStatus(status);
        taskPersistRepository.save(task);
    }
}
