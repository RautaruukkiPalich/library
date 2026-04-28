package com.app.modules.media.service;

import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.converter.MediaConverterFactory;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.metadata.MediaMetadataService;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MediaProcessingService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final TaskService taskService;

    private final MediaGetterRepository mediaGetterRepository;

    private final FileGetterRepository fileGetterRepository;

    private final MediaService mediaService;

    private final FileService fileService;
    private final MediaConverterFactory mediaConverterFactory;
    private final MediaMetadataService mediaMetadataService;

    @Transactional
    public void processTask(@NonNull UUID taskUuid) {

        MediaTask task = taskGetterRepository.getByUUID(taskUuid);

        log.info("run process task {}", task.getUuid());

        if (!task.getStatus().isPending()) {
            log.info("task {} status is {} not PENDING", task.getUuid(), task.getStatus());
            return;
        }

        taskService.setStatus(task, TaskStatus.PROCESSING);

        MediaTask savedTask = taskPersistRepository.saveNested(task);

        try {
            executeProcess(savedTask);
            taskService.setCompleteStatus(savedTask);
            taskPersistRepository.save(savedTask);
        } catch (Exception e) {
            taskService.setFailedStatus(savedTask, e.getMessage());
            taskPersistRepository.save(savedTask);
        }
    }

    private void executeProcess(@NonNull MediaTask task) throws IOException {
        String filePath = null;

        Media media = mediaGetterRepository.getByUuid(task.getMediaUuid());
        MediaFile original = media.getOriginal();
        ConversionParams cp = task.getConversionParams();

        if (!mediaMetadataService.canConvert(original.getMetadata(), cp)) {
            log.error("can not convert media {} to {}", media.getUuid(), cp);
            throw new RuntimeException("can not convert media");
        }

        if (!mediaConverterFactory.supports(cp.getTargetType())) {
            log.error("converters does not supports {} type", cp.getTargetType());
            throw new RuntimeException("no converter for current type");
        }

        try (InputStream source = fileGetterRepository.getByRelativePath(original.getPath())) {
            try (InputStream res = mediaConverterFactory.convert(source, cp)) {
                filePath = fileService.upload(res, media.getUuid(), cp.getTargetExtension());

                MediaMetadata metadata = mediaMetadataService.getMetadata(filePath);

                MediaFile mf = mediaService.createMediaFile(
                        original.getFilename(), cp.getTargetSize(), media, filePath, metadata);

                log.info("media file {} created for media {}", mf.getUuid(), media.getUuid());

                log.info("task {} executed success", task.getUuid());
            }
        } catch (Exception e) {
            log.error("failed execute process task {} cause {}", task.getUuid(), e.getMessage());
            cleanUpFile(task.getUuid(), filePath);
            throw e;
        }
    }

    private void cleanUpFile(@NonNull UUID taskUuid,
                             String filePath) {
        if (filePath == null) return;

        log.info("deleting file {} of task {}", filePath, taskUuid);
        fileService.delete(filePath);
    }
}
