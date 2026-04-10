package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaProcessingService;
import com.app.modules.media.converter.MediaConverter;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.UploadStatusType;
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
        if (task.getStatus() != UploadStatusType.PENDING) {
            log.warn("task={} already processing or completed, skipping", taskUuid);
            return;
        }

        updateTaskStatus(task, UploadStatusType.PROCESSING);

        MediaFile original = task.getMedia().getOriginal();
        if (original == null) {
            log.error("original file not found for task={}", taskUuid);
            updateTaskStatus(task, UploadStatusType.FAILED);
            return;
        }

        List<MediaFile> converted = new ArrayList<>();

        try {
            for (MediaSize size : sizesToConvert) {
                converted.add(mediaConverter.convert(original, size));
            }

            converted.forEach(mediaFilePersistRepository::save);

            updateTaskStatus(task, UploadStatusType.COMPLETED);

            long duration = System.currentTimeMillis() - startTime;
            log.info("task={} completed in {} ms, converted {} files", taskUuid, duration, converted.size());

        } catch (Exception e) {
            log.error("failed to process task={}", taskUuid, e);
            rollbackConvertedFiles(converted);
            updateTaskStatus(task, UploadStatusType.FAILED);

            long duration = System.currentTimeMillis() - startTime;
            log.info("task={} failed in {} ms", taskUuid, duration);
        }
    }

    private void updateTaskStatus(MediaTask task, UploadStatusType status) {
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
