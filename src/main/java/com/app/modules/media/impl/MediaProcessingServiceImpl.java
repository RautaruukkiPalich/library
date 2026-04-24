package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaProcessingService;
import com.app.modules.media.api.TaskService;
import com.app.modules.media.converter.media.MediaConverterFactory;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.metadata.MediaMetadataService;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MediaProcessingServiceImpl implements MediaProcessingService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;
    private final TaskService taskService;

    private final MediaGetterRepository mediaGetterRepository;
    private final MediaFilePersistRepository mediaFilePersistRepository;

    private final FileGetterRepository fileGetterRepository;

    private final FileService fileService;
    private final MediaConverterFactory mediaConverterFactory;
    private final MediaMetadataService mediaMetadataService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTask(@NonNull UUID taskUuid) {
        taskService.prepareTaskStatus(taskUuid, TaskStatus.PROCESSING);

        MediaTask task = taskGetterRepository.getByUUID(taskUuid);
        String filePath = null;

        try {
            Media media = mediaGetterRepository.getByUuid(task.getMediaUuid());
            MediaFile original = media.getOriginal();
            MediaMetadata md = task.getMediaMetadata();

            if (!original.compare(md)){
                throw new RuntimeException("height or width of original file is smaller");
            }

            InputStream source = fileGetterRepository.getByRelativePath(original.getPath());
            InputStream res = mediaConverterFactory.convert(source, md);

            log.debug("file converted");

            filePath = fileService.upload(
                    res,
                    media.getUuid(),
                    md.getExtension());

            log.debug("file upload");

            Long fileSize = fileService.fileSize(filePath);
            MediaMetadata metadata = mediaMetadataService.getMetadata(media.getMediaContent(), filePath);

            MediaFile mf = MediaFile.create(
                    original.getFilename(),
                    media,
                    fileSize,
                    filePath,
                    md
            );

            log.info("media file {} created for media={}, md={} {}x{}",
                    mf.getUuid(), media.getUuid(), metadata.getMediaSize(), metadata.getWidth(), metadata.getHeight());

            mediaFilePersistRepository.save(mf);

            log.debug("media file {} saved", mf.getUuid());

            task.setStatusCompleted();
            taskPersistRepository.save(task);
        } catch (Exception e) {
            log.error("failed execute process task cause {}", e.getMessage());

            task.setStatusFailed(e.getMessage());

            taskPersistRepository.save(task);
            if (filePath != null) {
                fileService.delete(filePath);
                log.debug("file path {} for task={} deleted cause exception", filePath, taskUuid);
            }
            log.error(e.toString());
        }
    }
}
