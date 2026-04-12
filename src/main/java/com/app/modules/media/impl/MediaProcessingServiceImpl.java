package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaProcessingService;
import com.app.modules.media.converter.MediaConverter;
import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.MediaFilePersistRepository;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.app.modules.media.enums.MediaSize.sizesToConvert;

@Service
@Slf4j
@AllArgsConstructor
public class MediaProcessingServiceImpl implements MediaProcessingService {
    private final TaskPersistRepository taskPersistRepository;
    private final TaskGetterRepository taskGetterRepository;

    private final MediaFilePersistRepository mediaFilePersistRepository;
    private final MediaConverter mediaConverter;

    private final FileService fileService;

    @Override
    @Transactional
    public void processTask(@NonNull UUID taskUuid) {
        long startTime = System.currentTimeMillis();

        MediaTask task = taskGetterRepository.getByUUID(taskUuid);
        if (task.getStatus() != TaskStatus.PENDING) {
            log.warn("task={} already processing or completed, skipping", taskUuid);
            return;
        }

        updateTaskStatus(task, TaskStatus.PROCESSING);

        MediaFile original = task.getMedia().getOriginal();
        if (original == null) {
            log.error("original file not found for task={}", taskUuid);
            updateTaskStatus(task, TaskStatus.FAILED);
            return;
        }

        List<MediaFile> converted = new ArrayList<>();

        try {
            for (MediaSize size : sizesToConvert) {
                MediaContent content = task.getMedia().getMediaContent();
                ConvertResultDTO res = mediaConverter.convert(
                        original.getMediaUuid(), original.getPath(),
                        size, content.getTargetExt());

                MediaFile mf = new MediaFile();
                mf.setUuid(UUID.randomUUID());
                mf.setFilename(original.getFilename());
                mf.setExtension(res.extension());
                mf.setContentType(content.getTargetContentType());
                mf.setMediaUuid(original.getMediaUuid());
                mf.setMediaSize(size);
                mf.setFileSize(res.size());
                mf.setPath(res.path());

                converted.add(mf);
            }

            converted.forEach(mediaFilePersistRepository::save);

            updateTaskStatus(task, TaskStatus.COMPLETED);

            long duration = System.currentTimeMillis() - startTime;
            log.info("task={} completed in {} ms, converted {} files", taskUuid, duration, converted.size());

        } catch (Exception e) {
            log.error("failed to process task={}", taskUuid, e);
            rollbackConvertedFiles(converted);
            updateTaskStatus(task, TaskStatus.FAILED);

            long duration = System.currentTimeMillis() - startTime;
            log.info("task={} failed in {} ms", taskUuid, duration);
        }
    }

    private void updateTaskStatus(MediaTask task, TaskStatus status) {
        task.setStatus(status);
        taskPersistRepository.save(task);
        log.debug("task={} status updated to: {}", task.getUuid(), status);
    }

    private void rollbackConvertedFiles(List<MediaFile> convertedFiles) {
        for (MediaFile mf : convertedFiles) {
            try {
                fileService.delete(mf.getPath());
                log.debug("deleted file after error: {}", mf.getPath());
            } catch (Exception deleteEx) {
                log.error("failed to delete file after error: {}", mf.getPath(), deleteEx);
            }
        }
    }
}
